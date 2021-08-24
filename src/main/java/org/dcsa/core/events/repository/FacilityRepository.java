package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Facility;
import org.dcsa.core.repository.ExtendedRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FacilityRepository extends ExtendedRepository<Facility, UUID> {

  Mono<Facility> findByUnLocationCodeAndFacilitySMGDCode(
      String uNLocationCode, String facilitySMDGCode);

  Mono<Facility> findByUnLocationCodeAndFacilityBICCode(
      String uNLocationCode, String facilityBICCode);
}
