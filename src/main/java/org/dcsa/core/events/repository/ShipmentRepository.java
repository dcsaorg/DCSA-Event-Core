package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Shipment;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ShipmentRepository extends ExtendedRepository<Shipment, UUID> {

  Flux<Shipment> findShipmentsByBookingIDNotNull(Pageable pageable);
  Flux<Shipment> findAllByCarrierBookingReference(String carrierBookingReference, Pageable pageable);
  Mono<Shipment> findByCarrierBookingReference(String carrierBookingReference);
}
