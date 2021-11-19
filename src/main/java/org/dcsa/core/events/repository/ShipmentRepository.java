package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Shipment;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ShipmentRepository extends ExtendedRepository<Shipment, UUID> {

  Flux<Shipment> findShipmentsByBookingIDNotNull(Pageable pageable);
  Flux<Shipment> findAllByCarrierBookingReference(String carrierBookingReference);
  Mono<Shipment> findByCarrierBookingReference(String carrierBookingReference);
}
