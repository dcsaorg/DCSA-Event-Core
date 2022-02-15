package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.DocumentParty;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface DocumentPartyRepository extends ExtendedRepository<DocumentParty, UUID> {
  Flux<DocumentParty> findByBookingID(UUID bookingID);

  Flux<DocumentParty> findByShippingInstructionID(String shippingInstructionID);

  Mono<Void> deleteByBookingID(UUID bookingID);

  Mono<Void> deleteByShippingInstructionID(String shippingInstructionID);
}
