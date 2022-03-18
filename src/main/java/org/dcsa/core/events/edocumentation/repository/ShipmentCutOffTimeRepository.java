package org.dcsa.core.events.edocumentation.repository;

import org.dcsa.core.events.model.ShipmentCutOffTime;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ShipmentCutOffTimeRepository
    extends ReactiveCrudRepository<ShipmentCutOffTime, UUID> {
  Flux<ShipmentCutOffTime> findAllByShipmentID(UUID shipmentID);
}
