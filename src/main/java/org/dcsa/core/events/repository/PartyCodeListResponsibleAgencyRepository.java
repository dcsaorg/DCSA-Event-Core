package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.PartyCodeListResponsibleAgency;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PartyCodeListResponsibleAgencyRepository
    extends ReactiveCrudRepository<PartyCodeListResponsibleAgency, UUID> {}
