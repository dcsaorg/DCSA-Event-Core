package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.events.repository.OperationsEventRepository;
import org.dcsa.core.events.service.OperationsEventService;
import org.dcsa.core.events.service.TransportCallTOService;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OperationsEventServiceImpl extends ExtendedBaseServiceImpl<OperationsEventRepository, OperationsEvent, UUID> implements OperationsEventService {

    private final OperationsEventRepository operationsEventRepository;
    private final TransportCallTOService transportCallTOService;

    @Override
    public OperationsEventRepository getRepository() {
        return operationsEventRepository;
    }

    private Mono<OperationsEvent> mapTransportCall(OperationsEvent operationsEvent) {
        return transportCallTOService
                .findById(operationsEvent.getTransportCallID())
                .doOnNext(operationsEvent::setTransportCall)
                .thenReturn(operationsEvent);
    }

    @Override
    public Mono<OperationsEvent> loadRelatedEntities(OperationsEvent event) {
        return mapTransportCall(event);
    }

    @Override
    public Mono<OperationsEvent> create(OperationsEvent operationsEvent) {
        operationsEvent.setTransportCallID(operationsEvent.getTransportCall().getTransportCallID());
        return super.save(operationsEvent);
    }
}
