package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.TransportEvent;
import org.dcsa.core.events.repository.TransportEventRepository;
import org.dcsa.core.events.service.TransportCallService;
import org.dcsa.core.events.service.TransportCallTOService;
import org.dcsa.core.events.service.TransportEventService;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TransportEventServiceImpl extends ExtendedBaseServiceImpl<TransportEventRepository, TransportEvent, UUID> implements TransportEventService {
    private final TransportEventRepository transportEventRepository;
    private final TransportCallService transportCallService;
    private final TransportCallTOService transportCallTOService;

    @Override
    public TransportEventRepository getRepository() {
        return transportEventRepository;
    }

    //Overriding base method here, as it marks empty results as an error, meaning we can't use switchOnEmpty()
    @Override
    public Mono<TransportEvent> findById(UUID id) {
        return getRepository().findById(id);
    }

    @Override
    public Mono<TransportEvent> loadRelatedEntities(TransportEvent event) {
        return mapTransportCall(event)
                .flatMap(transportEvent ->
                        transportCallService.findReferencesForTransportCallID(event.getTransportCallID())
                                .doOnNext(transportEvent::setReferences)
                                .then(transportCallService.findDocumentReferencesForTransportCallID(event.getTransportCallID()))
                                .doOnNext(transportEvent::setDocumentReferences)
                                .thenReturn(transportEvent)
                );
    }

    @Override
    public Mono<TransportEvent> mapTransportCall(TransportEvent transportEvent) {
        return transportCallTOService
                .findById(transportEvent.getTransportCallID())
                .doOnNext(transportEvent::setTransportCall)
                .thenReturn(transportEvent);
    }
}
