package org.dcsa.core.events.service;

import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.service.ExtendedBaseService;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OperationsEventService extends ExtendedBaseService<OperationsEvent, UUID> {
    Mono<OperationsEvent> loadRelatedEntities(OperationsEvent event);
}