package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Charge;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ChargeRepository extends ExtendedRepository<Charge, String> {
  Flux<Charge> findAllByShipmentID(UUID shipmentID);
}
