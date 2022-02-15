package org.dcsa.core.events.repository;

import org.dcsa.core.repository.ExtendedRepository;
import org.dcsa.core.events.model.Carrier;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CarrierRepository extends ReactiveCrudRepository<Carrier, UUID> {

    Mono<Carrier> findBySmdgCode(String smdgCode);
    Mono<Carrier> findByNmftaCode(String NmftaCode);
}
