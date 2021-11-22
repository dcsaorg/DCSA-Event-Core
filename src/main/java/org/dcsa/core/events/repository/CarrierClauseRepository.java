package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.CarrierClause;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface CarrierClauseRepository extends ExtendedRepository<CarrierClause, UUID> {
    Mono<CarrierClause> findByCarrierClauseID(UUID carrierClauseID);
}
