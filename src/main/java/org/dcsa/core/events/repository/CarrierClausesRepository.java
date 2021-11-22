package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.CarrierClause;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CarrierClausesRepository extends ExtendedRepository<CarrierClause, UUID> {}
