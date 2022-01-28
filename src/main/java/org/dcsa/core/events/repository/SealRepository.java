package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Seal;
import org.dcsa.core.repository.ExtendedRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface SealRepository extends ExtendedRepository<Seal, UUID> {
    Flux<Seal> findAllByShipmentEquipmentID(UUID shipmentEquipmentID);
}
