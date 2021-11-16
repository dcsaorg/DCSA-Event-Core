package org.dcsa.core.events.repository;

import org.dcsa.core.repository.ExtendedRepository;
import org.dcsa.core.events.model.Vessel;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

public interface VesselRepository extends ExtendedRepository<Vessel, UUID> {

    default Mono<Vessel> findByIdOrEmpty(UUID id) {
        if (Objects.isNull(id)) {
            return Mono.empty();
        } else {
            return findById(id);
        }
    }
}
