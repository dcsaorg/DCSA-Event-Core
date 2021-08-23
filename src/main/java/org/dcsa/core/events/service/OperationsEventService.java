package org.dcsa.core.events.service;

import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.repository.InsertAddonRepository;
import reactor.core.publisher.Mono;

public interface OperationsEventService extends EventService<OperationsEvent>, InsertAddonRepository<OperationsEvent> {
    Mono<OperationsEvent> loadRelatedEntities(OperationsEvent event);
}