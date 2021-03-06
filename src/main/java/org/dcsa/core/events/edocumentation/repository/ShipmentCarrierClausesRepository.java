package org.dcsa.core.events.edocumentation.repository;

import org.dcsa.core.events.model.ShipmentCarrierClause;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ShipmentCarrierClausesRepository
    extends ReactiveCrudRepository<ShipmentCarrierClause, UUID> {
  Flux<ShipmentCarrierClause> findAllByShipmentID(UUID shipmentID);
}
