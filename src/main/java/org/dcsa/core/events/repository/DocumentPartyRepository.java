package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.DocumentParty;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface DocumentPartyRepository extends ReactiveCrudRepository<DocumentParty, UUID> {
  Flux<DocumentParty> findByBookingID(UUID bookingID);

  Flux<DocumentParty> findByShippingInstructionID(UUID shippingInstructionID);

  Mono<Void> deleteByBookingID(UUID bookingID);

  Mono<Void> deleteByShippingInstructionID(UUID shippingInstructionID);
}
