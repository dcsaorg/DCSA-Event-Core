package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.ShipmentCarrierClause;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ShipmentCarrierClausesRepository
    extends ExtendedRepository<ShipmentCarrierClause, UUID> {
  Flux<ShipmentCarrierClause> findAllByShipmentID(UUID shipmentID);
}
