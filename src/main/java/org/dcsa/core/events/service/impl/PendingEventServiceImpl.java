package org.dcsa.core.events.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dcsa.core.events.model.EventSubscription;
import org.dcsa.core.events.model.PendingMessage;
import org.dcsa.core.events.repository.PendingEventRepository;
import org.dcsa.core.events.service.EventSubscriptionService;
import org.dcsa.core.events.service.GenericEventService;
import org.dcsa.core.events.service.PendingEventService;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PendingEventServiceImpl extends ExtendedBaseServiceImpl<PendingEventRepository, PendingMessage, UUID> implements PendingEventService {

    private static final int EVENTS_PER_BATCH_JOB = 10;
    // To make easier to do this with Flux which works on lists/arrays
    private static final Object DUMMY_OBJECT = new Object();
    private static final Object[] EVENT_ARRAY;

    static {
        EVENT_ARRAY = new Object[EVENTS_PER_BATCH_JOB];
        for (int i = 0 ; i < EVENTS_PER_BATCH_JOB ; i++) {
            EVENT_ARRAY[i] = DUMMY_OBJECT;
        }
    }

    private final PendingEventRepository pendingEventRepository;
    private final ReactiveTransactionManager transactionManager;
    private GenericEventService eventService;
    private final EventSubscriptionService eventSubscriptionService;
    private final MessageSignatureHandler messageSignatureHandler;

    private final ObjectMapper objectMapper;

    private Disposable processUnmappedEvent;
    private Disposable processPendingEventQueue;

    @Value("${dcsa.pendingEventService.parallel:4}")
    private int parallel;

    @Override
    public PendingEventRepository getRepository() {
        return pendingEventRepository;
    }

    @Scheduled(
            cron = "${dcsa.pendingEventService.backgroundTasks.processUnmappedEventQueue.cronSchedule:45 */1 * * * *}"
    )
    public synchronized void processUnmappedEventQueue() {
        if (!messageSignatureHandler.isPendingEventServiceEnabled()) {
            return;
        }
        if (processUnmappedEvent != null && !processUnmappedEvent.isDisposed()) {
            log.info("Skipping processUnmappedEventQueue task. Previous job is still on-going");
            return;
        }
        processUnmappedEvent = null;
        Instant start = Instant.now();
        TransactionalOperator transactionalOperator = TransactionalOperator.create(transactionManager);
        log.info("Starting processUnmappedEventQueue task");


        Mono<Tuple2<UUID, Long>> mapJob = pendingEventRepository.pollUnmappedEventID()
                .checkpoint("Fetched unmappedEvent event")
                .flatMap(eventService::findById)
                .flatMap(mappedEvent ->
                        Mono.zip(
                                Mono.just(mappedEvent.getEventID()),
                                eventSubscriptionService.findSubscriptionsFor(mappedEvent)
                                        .flatMap(eventSubscription -> {
                                            PendingMessage pendingMessage = new PendingMessage();
                                            pendingMessage.setEventID(mappedEvent.getEventID());
                                            pendingMessage.setSubscriptionID(eventSubscription.getSubscriptionID());
                                            try {
                                                pendingMessage.setPayload(objectMapper.writeValueAsString(mappedEvent));
                                            } catch (JsonProcessingException e) {
                                                return Mono.error(e);
                                            }
                                            return Mono.just(pendingMessage);
                                        }).concatMap(this::create)
                                        .count()
                                        .onErrorResume(ex -> {
                                            log.warn("Error processing event ID " + mappedEvent.getEventID(), ex);
                                            return Mono.just(-1L);
                                        })
                ));

        Mono<?> parallelJob = Flux.fromArray(EVENT_ARRAY)
                .parallel(parallel)
                .flatMap(ignored -> transactionalOperator.transactional(mapJob))
                .sequential()
                .collectList()
                .doOnSuccess(tupleList -> {
                    Instant finish = Instant.now();
                    Duration duration = Duration.between(start, finish);
                    if (tupleList != null && !tupleList.isEmpty()) {
                        for (Tuple2<UUID, Long> tuple : tupleList) {
                            UUID eventID = tuple.getT1();
                            long count = tuple.getT2();
                            if (count > 0) {
                                log.info("Successfully generated " + count + " pending event(s) for event "
                                        + eventID + ".");
                            } else if (count == 0) {
                                log.info("No subscribers for event " + eventID);
                            }
                            // Ignore count < 0; those are errors and have already been logged at this stage.
                        }
                        log.info("The processUnmappedEventQueue job took " + duration);
                    } else {
                        log.info("No events to process. The processUnmappedEventQueue job took " + duration);
                    }
                });

        processUnmappedEvent = parallelJob.doFinally((ignored) -> {
            if (processUnmappedEvent != null) {
                processUnmappedEvent.dispose();
            }
            processUnmappedEvent = null;
        }).subscribe();
    }

    @Scheduled(
            cron = "${dcsa.pendingEventService.backgroundTasks.processPendingEventQueue.cronSchedule:15 */1 * * * *}"
    )
    public synchronized void processPendingEventQueue() {
        if (!messageSignatureHandler.isPendingEventServiceEnabled()) {
            return;
        }
        if (processPendingEventQueue != null && !processPendingEventQueue.isDisposed()) {
            log.info("Skipping processPendingEventQueue task. Previous job is still on-going");
            return;
        }
        processPendingEventQueue = null;
        Instant start = Instant.now();
        TransactionalOperator transactionalOperator = TransactionalOperator.create(transactionManager);
        log.info("Starting processPendingEventQueue task");

        Mono<MessageSignatureHandler.SubmissionResult<EventSubscription>> mapJob = pendingEventRepository.pollPendingEvent()
                .checkpoint("Fetched pending event")
                .flatMap(pendingMessage ->
                        eventSubscriptionService.findById(pendingMessage.getSubscriptionID())
                            .flatMap(eventSubscription ->
                                messageSignatureHandler.emitMessage(eventSubscription, Flux.just(pendingMessage))
                            )
                ).flatMap(submissionResult -> {
                    if (submissionResult.isSuccessful()) {
                        return eventSubscriptionService.update(submissionResult.getEventSubscription())
                                .thenReturn(submissionResult);
                    }
                    return eventSubscriptionService.update(submissionResult.getEventSubscription())
                            .thenMany(Flux.fromIterable(submissionResult.getPendingMessages()))
                            .concatMap(pendingEventRepository::save)
                            .then(Mono.just(submissionResult));
                });

        // Note due to database locking, parallel only operates on distinct subscription IDs.  Accordingly, the parallel
        // only "matters" if there are multiple subscriptions.  On the positive side, it means we avoid conflicts when
        // they will commit.
        Mono<?> parallelJob = Flux.fromArray(EVENT_ARRAY)
                .parallel(parallel)
                .flatMap(ignored -> transactionalOperator.transactional(mapJob))
                .sequential()
                .collectList()
                .doOnSuccess(submissionResults -> {
                    Instant finish = Instant.now();
                    Duration duration = Duration.between(start, finish);
                    for (MessageSignatureHandler.SubmissionResult<EventSubscription> submissionResult : submissionResults) {
                        if (submissionResult.isSuccessful()) {
                            for (PendingMessage pendingMessage : submissionResult.getPendingMessages()) {
                                log.info("Submitted " + pendingMessage.getEventID() + " to subscription "
                                        + submissionResult.getEventSubscription().getSubscriptionID());
                            }
                        } else {
                            log.info("Delivery of pending messages to "
                                    + submissionResult.getEventSubscription().getSubscriptionID()
                                    + " failed (will retry later).");
                        }
                    }
                    if (submissionResults.isEmpty()) {
                        log.info("No pending messages that can be send at the moment. The processPendingEventQueue job took "
                                + duration);
                    } else {
                        log.info("The processPendingEventQueue job took " + duration);
                    }
                });
        processPendingEventQueue = parallelJob.doFinally((ignored) -> {
            if (processPendingEventQueue != null) {
                processPendingEventQueue.dispose();
            }
            processPendingEventQueue = null;
        }).subscribe();
    }

    @Lazy
    @Autowired
    public void setEventService(GenericEventService eventService) {
        this.eventService = eventService;
    }
}
