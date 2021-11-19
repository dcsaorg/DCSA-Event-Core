package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.ShipmentCutOffTime;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ShipmentCutOffTimeRepository extends ExtendedRepository<ShipmentCutOffTime, UUID> {
  Flux<ShipmentCutOffTime> findAllByShipmentID(UUID shipmentID);
}
