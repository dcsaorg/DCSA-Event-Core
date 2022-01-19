package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.ShipmentLocation;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ShipmentLocationRepository extends ExtendedRepository<ShipmentLocation, UUID> {
  Flux<ShipmentLocation> findByBookingID(UUID bookingID);
  Flux<ShipmentLocation> findByShipmentID(UUID shipmentID);
  Mono<Void> deleteByBookingID(UUID bookingID);
}
