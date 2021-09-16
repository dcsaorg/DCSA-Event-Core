package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.PartyIdentifyingCode;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PartyIdentifyingCodeRepository
    extends ReactiveCrudRepository<PartyIdentifyingCode, UUID> {}
