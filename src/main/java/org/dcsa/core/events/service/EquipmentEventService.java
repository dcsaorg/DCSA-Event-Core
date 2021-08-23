package org.dcsa.core.events.service;

import org.dcsa.core.events.model.EquipmentEvent;
import org.dcsa.core.repository.InsertAddonRepository;
import reactor.core.publisher.Mono;

public interface EquipmentEventService extends EventService<EquipmentEvent>, InsertAddonRepository<EquipmentEvent> {
    Mono<EquipmentEvent> loadRelatedEntities(EquipmentEvent event);

}
