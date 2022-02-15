package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Seal;
import org.dcsa.core.repository.ExtendedRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SealRepository extends ExtendedRepository<Seal, UUID> {
    Mono<Void> deleteAllByShipmentEquipmentID(UUID shipmentEquipmentID);
    Flux<Seal> findAllByShipmentEquipmentID(UUID shipmentEquipmentID);
}
