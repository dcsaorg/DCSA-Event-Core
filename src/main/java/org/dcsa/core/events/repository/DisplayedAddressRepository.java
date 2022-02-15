package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.DisplayedAddress;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface DisplayedAddressRepository extends ExtendedRepository<DisplayedAddress, UUID> {
  Mono<Void> deleteAllByDocumentPartyID(UUID documentPartyID);
  Flux<DisplayedAddress> findByDocumentPartyIDOrderByAddressLineNumber(UUID documentPartyID);
}
