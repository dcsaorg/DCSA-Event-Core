package org.dcsa.core.events.service;

import org.dcsa.core.events.model.TransportEvent;
import reactor.core.publisher.Flux;

public interface TransportEventService extends EventService<TransportEvent> {
    Flux<TransportEvent> mapTransportCall(Flux<TransportEvent> transportEvents);
}
