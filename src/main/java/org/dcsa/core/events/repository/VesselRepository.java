package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Vessel;
import org.dcsa.core.repository.ExtendedRepository;
import org.dcsa.core.repository.InsertAddonRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface VesselRepository extends ExtendedRepository<Vessel, UUID>, InsertAddonRepository<Vessel> {
    Mono<Vessel> findByVesselIMONumber(final String vesselIMONumber);
}
