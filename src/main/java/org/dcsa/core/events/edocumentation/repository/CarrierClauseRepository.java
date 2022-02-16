package org.dcsa.core.events.edocumentation.repository;

import org.dcsa.core.events.model.CarrierClause;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CarrierClauseRepository extends ReactiveCrudRepository<CarrierClause, UUID> {}
