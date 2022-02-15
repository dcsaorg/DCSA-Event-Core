package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.ShipmentLocation;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ShipmentLocationRepository extends ReactiveCrudRepository<ShipmentLocation, UUID> {
  Flux<ShipmentLocation> findByBookingID(UUID bookingID);
  Flux<ShipmentLocation> findByShipmentID(UUID shipmentID);
  Mono<Void> deleteByBookingID(UUID bookingID);
}
