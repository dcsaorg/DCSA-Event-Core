package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.ShipmentCutOffTime;
import org.dcsa.core.repository.ExtendedRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ShipmentCutOffTimeRepository extends ExtendedRepository<ShipmentCutOffTime, UUID> {
  Flux<ShipmentCutOffTime> findAllByShipmentID(UUID shipmentID);
}
