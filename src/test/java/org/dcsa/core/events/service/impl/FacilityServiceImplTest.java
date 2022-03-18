package org.dcsa.core.events.service.impl;

import org.dcsa.core.events.model.Facility;
import org.dcsa.core.events.model.enums.FacilityCodeListProvider;
import org.dcsa.core.events.repository.FacilityRepository;
import org.dcsa.core.exception.ConcreteRequestErrorMessageException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test for FacilityService implementation")
class FacilityServiceImplTest {

  @Mock FacilityRepository facilityRepository;

  @InjectMocks FacilityServiceImpl facilityService;

  Facility facility;

  @BeforeEach
  void init() {
    initEntities();
  }

  private void initEntities() {
    facility = new Facility();
    facility.setFacilityID(UUID.randomUUID());
    facility.setFacilityName("Some facility");
    facility.setFacilitySMDGCode("x".repeat(6));
    facility.setFacilityBICCode("x".repeat(4));
    facility.setUnLocationCode("x".repeat(5));
  }

  @Test
  @DisplayName("Test findByIdOrEmpty with ID")
  void testFindFacilityByID() {

    when(facilityRepository.findByIdOrEmpty(any(UUID.class))).thenReturn(Mono.just(facility));

    StepVerifier.create(facilityService.findByIdOrEmpty(facility.getFacilityID()))
        .assertNext(
            f -> {
              assertEquals(facility.getFacilityID(), f.getFacilityID());
              assertEquals(facility.getFacilityBICCode(), f.getFacilityBICCode());
              assertEquals(facility.getUnLocationCode(), f.getUnLocationCode());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test findByUNLocationCodeAndFacilityCode with BIC code")
  void testFindFacilityByBIC() {

    when(facilityRepository.findByUnLocationCodeAndFacilityBICCode(any(), any()))
        .thenReturn(Mono.just(facility));

    StepVerifier.create(
            facilityService.findByUNLocationCodeAndFacilityCode(
                facility.getUnLocationCode(),
                FacilityCodeListProvider.BIC,
                facility.getFacilityBICCode()))
        .assertNext(
            f -> {
              verify(facilityRepository, times(0))
                  .findByUnLocationCodeAndFacilitySMDGCode(any(), any());
              verify(facilityRepository).findByUnLocationCodeAndFacilityBICCode(any(), any());

              assertEquals(facility.getFacilityID(), f.getFacilityID());
              assertEquals(facility.getFacilityBICCode(), f.getFacilityBICCode());
              assertEquals(facility.getUnLocationCode(), f.getUnLocationCode());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test findByUNLocationCodeAndFacilityCode with SMDG code")
  void testFindFacilityBySMDG() {

    when(facilityRepository.findByUnLocationCodeAndFacilitySMDGCode(any(), any()))
        .thenReturn(Mono.just(facility));

    StepVerifier.create(
            facilityService.findByUNLocationCodeAndFacilityCode(
                facility.getUnLocationCode(),
                FacilityCodeListProvider.SMDG,
                facility.getFacilitySMDGCode()))
        .assertNext(
            f -> {
              verify(facilityRepository).findByUnLocationCodeAndFacilitySMDGCode(any(), any());
              verify(facilityRepository, times(0))
                  .findByUnLocationCodeAndFacilityBICCode(any(), any());

              assertEquals(facility.getFacilityID(), f.getFacilityID());
              assertEquals(facility.getFacilityBICCode(), f.getFacilityBICCode());
              assertEquals(facility.getUnLocationCode(), f.getUnLocationCode());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test findByUNLocationCodeAndFacilityCode with non-existent SMDG code")
  void testFindFacilityWithIncorrectFacilitySMDGCode() {

    when(facilityRepository.findByUnLocationCodeAndFacilitySMDGCode(any(), any()))
        .thenReturn(Mono.empty());

    StepVerifier.create(
            facilityService.findByUNLocationCodeAndFacilityCode(
                facility.getUnLocationCode(),
                FacilityCodeListProvider.SMDG,
                facility.getFacilitySMDGCode()))
        .expectErrorSatisfies(
            throwable -> {
              Assertions.assertTrue(throwable instanceof ConcreteRequestErrorMessageException);
              assertEquals(
                  "Cannot find any facility with UNLocationCode + Facility code: "
                      + facility.getUnLocationCode()
                      + ", "
                      + facility.getFacilitySMDGCode(),
                  throwable.getMessage());
            })
        .verify();
  }

  @Test
  @DisplayName("Test findByUNLocationCodeAndFacilityCode with null unLocationCode")
  void testFindFacilityWithNullUnLocationCode() {

    StepVerifier.create(
            facilityService.findByUNLocationCodeAndFacilityCode(
                null, FacilityCodeListProvider.BIC, facility.getFacilityBICCode()))
        .expectErrorSatisfies(
            throwable -> {
              Assertions.assertTrue(throwable instanceof ConcreteRequestErrorMessageException);
              assertEquals("The attribute unLocationCode cannot be null", throwable.getMessage());
            })
        .verify();
  }

  @Test
  @DisplayName("Test findByUNLocationCodeAndFacilityCode with null facility code")
  void testFindFacilityWithNullFacilityCode() {

    StepVerifier.create(
            facilityService.findByUNLocationCodeAndFacilityCode(
                facility.getUnLocationCode(), FacilityCodeListProvider.BIC, null))
        .expectErrorSatisfies(
            throwable -> {
              Assertions.assertTrue(throwable instanceof ConcreteRequestErrorMessageException);
              assertEquals("The attribute facilityCode cannot be null", throwable.getMessage());
            })
        .verify();
  }
}
