package org.dcsa.core.events.service;

import org.dcsa.core.service.ExtendedBaseService;
import org.dcsa.core.events.model.Vessel;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface VesselService extends ExtendedBaseService<Vessel, UUID> {

    Mono<Vessel> create(Vessel vessel);

    Mono<Vessel> update(Vessel vessel);

    Mono<Vessel> findByVesselIMONumber(String vesselIMONumber);

    Mono<Vessel> findById(UUID vesselID);
}
