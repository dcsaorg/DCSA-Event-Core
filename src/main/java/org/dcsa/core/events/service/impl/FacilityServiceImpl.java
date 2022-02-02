package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Facility;
import org.dcsa.core.events.model.enums.FacilityCodeListProvider;
import org.dcsa.core.events.repository.FacilityRepository;
import org.dcsa.core.events.service.FacilityService;
import org.dcsa.core.exception.ConcreteRequestErrorMessageException;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;
import java.util.function.BiFunction;

@RequiredArgsConstructor
@Service
public class FacilityServiceImpl extends ExtendedBaseServiceImpl<FacilityRepository, Facility, UUID>
    implements FacilityService {

  private final FacilityRepository facilityRepository;

  @Override
  public FacilityRepository getRepository() {
    return facilityRepository;
  }

  @Override
  public Mono<Facility> findByUNLocationCodeAndFacilityCode(
      String unLocationCode,
      FacilityCodeListProvider facilityCodeListProvider,
      String facilityCode) {
    BiFunction<String, String, Mono<Facility>> method;
    switch (Objects.requireNonNull(facilityCodeListProvider, "The attribute facilityCodeListProvider cannot be null.")) {
      case SMDG:
        method = facilityRepository::findByUnLocationCodeAndFacilitySMDGCode;
        break;
      case BIC:
        method = facilityRepository::findByUnLocationCodeAndFacilityBICCode;
        break;
      default:
        throw ConcreteRequestErrorMessageException.invalidParameter("Unsupported facility code list provider: " + facilityCodeListProvider);
    }
    return method
        .apply(
            Objects.requireNonNull(unLocationCode, "The attribute unLocationCode cannot be null."),
            Objects.requireNonNull(facilityCode, "The attribute facilityCode be null."))
        .switchIfEmpty(
            Mono.error(ConcreteRequestErrorMessageException.invalidParameter(
                    "Cannot find any facility with UNLocationCode + Facility code: "
                        + unLocationCode
                        + ", "
                        + facilityCode)));
  }

  @Override
  public Mono<Facility> findByIdOrEmpty(UUID id) {
    return facilityRepository.findByIdOrEmpty(id);
  }
}
