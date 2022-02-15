package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.RequestedEquipment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface RequestedEquipmentRepository extends ReactiveCrudRepository<RequestedEquipment, UUID> {
  Flux<RequestedEquipment> findByBookingID(UUID bookingID);
  Mono<Void> deleteByBookingID(UUID bookingID);
}
