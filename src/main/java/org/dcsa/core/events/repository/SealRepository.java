package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Seal;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface SealRepository extends ReactiveCrudRepository<Seal, UUID> {
    Flux<Seal> findAllByShipmentEquipmentID(UUID shipmentEquipmentID);
}
