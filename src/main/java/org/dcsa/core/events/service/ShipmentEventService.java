package org.dcsa.core.events.service;

import org.dcsa.core.events.model.ShipmentEvent;
import reactor.core.publisher.Mono;

public interface ShipmentEventService extends EventService<ShipmentEvent> {
    Mono<ShipmentEvent> loadRelatedEntities(ShipmentEvent event);
}
