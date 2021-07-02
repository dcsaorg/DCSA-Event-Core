package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.TransportEvent;
import org.dcsa.core.events.service.TransportCallService;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.core.events.repository.TransportEventRepository;
import org.dcsa.core.events.service.TransportEventService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TransportEventServiceImpl extends ExtendedBaseServiceImpl<TransportEventRepository, TransportEvent, UUID> implements TransportEventService {
    private final TransportEventRepository transportEventRepository;
    private final TransportCallService transportCallService;


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
    public Flux<TransportEvent> mapTransportCall(Flux<TransportEvent> transportEvents){
        return transportEvents
                .flatMap(transportEvent ->
                        transportCallService.findById(transportEvent.getTransportCallID())
                                .doOnNext(transportEvent::setTransportCall)
                                .thenReturn(transportEvent));
    }
}
