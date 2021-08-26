package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Facility;
import org.dcsa.core.events.model.enums.FacilityCodeListProvider;
import org.dcsa.core.events.repository.FacilityRepository;
import org.dcsa.core.events.service.FacilityService;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;
import java.util.function.BiFunction;

@RequiredArgsConstructor
@Service
public class FacilityServiceImpl extends ExtendedBaseServiceImpl<FacilityRepository, Facility, UUID> implements FacilityService {

    private final FacilityRepository facilityRepository;

    @Override
    public FacilityRepository getRepository() {
        return facilityRepository;
    }

    @Override
    public Mono<Facility> findByUNLocationCodeAndFacilityCode(String unLocationCode, FacilityCodeListProvider facilityCodeListProvider, String facilityCode) {
        BiFunction<String, String, Mono<Facility>> method;
        switch (Objects.requireNonNull(facilityCodeListProvider, "facilityCodeListProvider")) {
            case SMDG:
                method = facilityRepository::findByUnLocationCodeAndFacilitySMGDCode;
                break;
            case BIC:
                method = facilityRepository::findByUnLocationCodeAndFacilityBICCode;
                break;
            default:
                throw new CreateException("Unsupported facility code list provider: " + facilityCodeListProvider);
        }
        return method.apply(
                    Objects.requireNonNull(unLocationCode, "unLocationCode"),
                    Objects.requireNonNull(facilityCode, "facilityCode")
                ).switchIfEmpty(Mono.error(new CreateException("Cannot find any facility with UNLocationCode + Facility code: "
                        + unLocationCode + ", " + facilityCode)));
    }
}
