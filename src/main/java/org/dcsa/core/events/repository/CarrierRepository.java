package org.dcsa.core.events.repository;

import org.dcsa.core.repository.ExtendedRepository;
import org.dcsa.core.events.model.Carrier;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CarrierRepository extends ExtendedRepository<Carrier, UUID> {

    @Query("SELECT carrier.*"
            + "  FROM carrier"
            + "  WHERE carrier.smdg_code =:smdgCode "
    )
    Mono<Carrier> getCarrierBySMdgCode(String smdgCode);

    @Query("SELECT carrier FROM carrier WHERE carrier.nmfta_code =:NmftaCode")
    Mono<Carrier> getCarrierByNmftaCode(String NmftaCode);
}

