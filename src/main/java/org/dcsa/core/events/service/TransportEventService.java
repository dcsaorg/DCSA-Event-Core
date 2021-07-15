package org.dcsa.core.events.service;

import org.dcsa.core.events.model.TransportEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransportEventService extends EventService<TransportEvent> {
    Flux<TransportEvent> mapTransportCall(Flux<TransportEvent> transportEvents);

    Mono<TransportEvent> mapReferences(TransportEvent transportEvents);

    Mono<TransportEvent> mapDocumentReferences(TransportEvent transportEvent);
}
