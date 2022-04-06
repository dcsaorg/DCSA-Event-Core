package org.dcsa.core.events.edocumentation.repository;

import org.dcsa.core.events.model.Charge;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ChargeRepository extends ReactiveCrudRepository<Charge, String> {
  Flux<Charge> findAllByShipmentID(UUID shipmentID);

  Flux<Charge> findAllByTransportDocumentID(UUID transportDocumentID);
}
