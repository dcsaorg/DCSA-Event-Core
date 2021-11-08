package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Shipment;
import org.dcsa.core.repository.ExtendedRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ShipmentRepository extends ExtendedRepository<Shipment, UUID> {
  Mono<Shipment> findByCarrierBookingReferenceID(String carrierBookingReference);
}
