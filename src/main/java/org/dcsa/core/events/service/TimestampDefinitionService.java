package org.dcsa.core.events.service;

import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.events.model.TimestampDefinition;
import org.dcsa.core.service.QueryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TimestampDefinitionService extends QueryService<TimestampDefinition, String> {

    Mono<OperationsEvent> markOperationsEventAsTimestamp(OperationsEvent operationsEvent);

    Flux<TimestampDefinition> findAll();
}