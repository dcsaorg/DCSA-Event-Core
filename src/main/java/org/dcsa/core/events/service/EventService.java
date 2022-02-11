package org.dcsa.core.events.service;

import org.dcsa.core.events.model.Event;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.service.ExtendedBaseService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EventService<T extends Event> {
    Mono<T> findById(UUID id);
    Flux<T> findAllExtended(ExtendedRequest<T> extendedRequest);
}
