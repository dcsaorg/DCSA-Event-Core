package org.dcsa.core.events.service;

import org.dcsa.core.events.model.OperationsEvent;
import reactor.core.publisher.Mono;

public interface OperationsEventService extends EventService<OperationsEvent> {
    Mono<OperationsEvent> loadRelatedEntities(OperationsEvent event);

    Mono<OperationsEvent> create(OperationsEvent operationsEvent);
}