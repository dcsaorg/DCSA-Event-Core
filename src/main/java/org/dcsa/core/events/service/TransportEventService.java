package org.dcsa.core.events.service;

import org.dcsa.core.events.model.TransportEvent;
import reactor.core.publisher.Mono;

public interface TransportEventService extends EventService<TransportEvent> {
    Mono<TransportEvent> loadRelatedEntities(TransportEvent event);

    Mono<TransportEvent> mapTransportCall(TransportEvent transportEvent);
}
