package org.dcsa.core.events.service;

import org.dcsa.core.events.model.Facility;
import org.dcsa.core.events.model.enums.FacilityCodeListProvider;
import org.dcsa.core.service.QueryService;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FacilityService extends QueryService<Facility, UUID> {

  Mono<Facility> findByUNLocationCodeAndFacilityCode(
      String unLocationCode,
      FacilityCodeListProvider facilityCodeListProvider,
      String facilityCode);

  Mono<Facility> findByIdOrEmpty(UUID id);
  Mono<Facility> findById(UUID uuid);
}
