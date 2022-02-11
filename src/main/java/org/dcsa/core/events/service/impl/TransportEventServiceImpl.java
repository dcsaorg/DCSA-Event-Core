package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.TransportEvent;
import org.dcsa.core.events.model.UnmappedEvent;
import org.dcsa.core.events.repository.TransportEventRepository;
import org.dcsa.core.events.repository.UnmappedEventRepository;
import org.dcsa.core.events.service.TransportCallService;
import org.dcsa.core.events.service.TransportCallTOService;
import org.dcsa.core.events.service.TransportEventService;
import org.dcsa.core.exception.GetException;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TransportEventServiceImpl extends ExtendedBaseServiceImpl<TransportEventRepository, TransportEvent, UUID> implements TransportEventService {
    private final TransportEventRepository transportEventRepository;
    private final TransportCallService transportCallService;
    private final TransportCallTOService transportCallTOService;
    private final UnmappedEventRepository unmappedEventRepository;

    @Value("${dcsa.events.transport-event-map-references-and-document-references:true}")
    private boolean mapReferences;

    @Override
    public TransportEventRepository getRepository() {
        return transportEventRepository;
    }

    @Override
    public Mono<TransportEvent> findById(UUID id) {
        return transportEventRepository.findById(id);
    }

    @Override
    public Mono<TransportEvent> loadRelatedEntities(TransportEvent event) {
        if (mapReferences) {
            return mapTransportCall(event)
                    .flatMap(transportEvent ->
                            transportCallService.findReferencesForTransportCallID(event.getTransportCallID())
                                    .doOnNext(transportEvent::setReferences)
                                    .then(transportCallService.findDocumentReferencesForTransportCallID(event.getTransportCallID()))
                                    .doOnNext(transportEvent::setDocumentReferences)
                                    .thenReturn(transportEvent)
                    );
        }
        return mapTransportCall(event);
    }

    @Override
    public Mono<TransportEvent> mapTransportCall(TransportEvent transportEvent) {
        return transportCallTOService
                .findById(transportEvent.getTransportCallID())
                .doOnNext(transportEvent::setTransportCall)
                .thenReturn(transportEvent);
    }

    @Override
    public Mono<TransportEvent> create(TransportEvent transportEvent) {
        return transportEventRepository.save(transportEvent).flatMap(
                te -> {
                    UnmappedEvent unmappedEvent = new UnmappedEvent();
                    unmappedEvent.setNewRecord(true);
                    unmappedEvent.setEventID(te.getEventID());
                    unmappedEvent.setEnqueuedAtDateTime(te.getEventCreatedDateTime());
                    return unmappedEventRepository.save(unmappedEvent);
                }).thenReturn(transportEvent);
    }
}
