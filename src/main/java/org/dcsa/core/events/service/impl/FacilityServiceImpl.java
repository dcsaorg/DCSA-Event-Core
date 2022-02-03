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
import org.springframework.test.web.servlet.result.FlashAttributeResultMatchers;
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

    if (unLocationCode == null)
      return Mono.error(
          ConcreteRequestErrorMessageException.invalidParameter(
              "The attribute unLocationCode cannot be null"));
    if (facilityCode == null)
      return Mono.error(
          ConcreteRequestErrorMessageException.invalidParameter(
              "The attribute facilityCode cannot be null"));

    return Mono.just(facilityCodeListProvider)
        .flatMap(
            facilityCodeListProvider1 -> {
              if (facilityCodeListProvider1 == FacilityCodeListProvider.SMDG) {
                return facilityRepository.findByUnLocationCodeAndFacilitySMDGCode(
                    unLocationCode, facilityCode);
              } else {
                return facilityRepository.findByUnLocationCodeAndFacilityBICCode(
                    unLocationCode, facilityCode);
              }
            })
        .switchIfEmpty(
            Mono.error(
                ConcreteRequestErrorMessageException.invalidParameter(
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
