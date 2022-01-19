package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Facility;
import org.dcsa.core.repository.ExtendedRepository;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

public interface FacilityRepository extends ExtendedRepository<Facility, UUID> {

  Mono<Facility> findByUnLocationCodeAndFacilitySMDGCode(
      String uNLocationCode, String facilitySMDGCode);

  Mono<Facility> findByUnLocationCodeAndFacilityBICCode(
      String uNLocationCode, String facilityBICCode);

  default Mono<Facility> findByIdOrEmpty(UUID id) {
    if (Objects.isNull(id)) {
      return Mono.empty();
    } else {
      return findById(id);
    }
  }
}
