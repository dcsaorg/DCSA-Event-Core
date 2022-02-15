package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.CarrierClause;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface CarrierClauseRepository extends ReactiveCrudRepository<CarrierClause, UUID> {
}
