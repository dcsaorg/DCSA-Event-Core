package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.DisplayedAddress;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface DisplayedAddressRepository extends ReactiveCrudRepository<DisplayedAddress, UUID> {
  Flux<DisplayedAddress> findByDocumentPartyIDOrderByAddressLineNumber(UUID documentPartyID);
}
