package org.dcsa.core.events.service;

import org.dcsa.core.events.model.TransportEvent;
import reactor.core.publisher.Mono;

public interface TransportEventService extends EventService<TransportEvent> {
    Mono<TransportEvent> mapTransportCall(TransportEvent transportEvent);

    Mono<TransportEvent> mapReferences(TransportEvent transportEvent);

    Mono<TransportEvent> mapDocumentReferences(TransportEvent transportEvent);
}
