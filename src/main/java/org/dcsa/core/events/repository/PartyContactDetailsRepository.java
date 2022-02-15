package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.PartyContactDetails;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface PartyContactDetailsRepository extends ReactiveCrudRepository<PartyContactDetails, UUID> {
  Flux<PartyContactDetails> findByPartyID(String partyID);
}
