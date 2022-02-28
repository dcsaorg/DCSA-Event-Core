package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.UnLocation;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UnLocationRepository extends ReactiveCrudRepository<UnLocation, String> {
}
