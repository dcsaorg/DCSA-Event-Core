package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.PartyContactDetails;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface PartyContactDetailsRepository
    extends ExtendedRepository<PartyContactDetails, UUID> {
  Flux<PartyContactDetails> findByPartyID(String partyID);
}
