package org.dcsa.core.events.service;

import org.dcsa.core.events.model.Facility;
import org.dcsa.core.events.model.enums.FacilityCodeListProvider;
import org.dcsa.core.service.ExtendedBaseService;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FacilityService extends ExtendedBaseService<Facility, UUID> {
    Mono<Facility> findByUNLocationCodeAndFacilityCode(String unLocationCode, FacilityCodeListProvider facilityCodeListProvider, String facilityCode);
}
