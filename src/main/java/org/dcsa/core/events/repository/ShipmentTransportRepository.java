package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.ShipmentTransport;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ShipmentTransportRepository extends ExtendedRepository<ShipmentTransport, UUID> {

  Flux<ShipmentTransport> findAllByBookingID(UUID bookingId);

  Flux<ShipmentTransport> findAllByShipmentID(UUID shipmentId);
}
