package org.dcsa.core.events.service;

import org.dcsa.core.events.model.EquipmentEvent;
import reactor.core.publisher.Mono;

public interface EquipmentEventService extends EventService<EquipmentEvent> {

    Mono<EquipmentEvent> loadRelatedEntities(EquipmentEvent event);

    Mono<EquipmentEvent> create(EquipmentEvent equipmentEvent);
}
