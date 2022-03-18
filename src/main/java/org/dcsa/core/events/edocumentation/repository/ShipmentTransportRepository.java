package org.dcsa.core.events.edocumentation.repository;

import org.dcsa.core.events.model.ShipmentTransport;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ShipmentTransportRepository extends ReactiveCrudRepository<ShipmentTransport, UUID> {

  Flux<ShipmentTransport> findAllByShipmentID(UUID shipmentId);
}
