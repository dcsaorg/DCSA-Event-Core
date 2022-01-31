package org.dcsa.core.events.service.impl;

import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.enums.ReferenceTypeCode;
import org.dcsa.core.events.model.transferobjects.ReferenceTO;
import org.dcsa.core.events.repository.ReferenceRepository;
import org.dcsa.core.exception.CreateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test for ReferenceService implementation")
public class ReferenceServiceImplTest {

  @Mock ReferenceRepository referenceRepository;

  @InjectMocks ReferenceServiceImpl referenceService;

  ReferenceTO referenceTO;

  Reference reference;

  @BeforeEach
  void init() {

    initEntities();

    initTO();
  }

  private void initEntities() {
    reference = new Reference();
    reference.setReferenceValue("test");
    reference.setReferenceType(ReferenceTypeCode.FF);
  }

  private void initTO() {
    referenceTO = new ReferenceTO();
    referenceTO.setReferenceValue(reference.getReferenceValue());
    referenceTO.setReferenceType(reference.getReferenceType());
  }

  @Test
  @DisplayName("Test create reference with bookingID")
  @SuppressWarnings("unchecked")
  void testWithBookingID() {
    UUID bookingID = UUID.randomUUID();
    reference.setBookingID(bookingID);

    when(referenceRepository.saveAll(any(Flux.class))).thenReturn(Flux.just(reference));

    ArgumentCaptor<Flux<Reference>> argumentCaptor = ArgumentCaptor.forClass(Flux.class);

    StepVerifier.create(
            referenceService.createReferencesByBookingIDAndTOs(bookingID, Collections.singletonList(referenceTO)))
        .assertNext(
            referenceTOS -> {
              verify(referenceRepository).saveAll(any(Flux.class));
              assertTrue(referenceTOS.isPresent());
              assertNotNull(referenceTOS.get().get(0).getReferenceValue());
              assertNotNull(referenceTOS.get().get(0).getReferenceValue());
              verify(referenceRepository).saveAll(argumentCaptor.capture());

              Reference capturedArgument = Objects.requireNonNull(argumentCaptor.getValue().blockFirst());
              assertEquals(bookingID, capturedArgument.getBookingID());
              assertNull(capturedArgument.getShippingInstructionID());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test create reference with shippingInstructionID")
  @SuppressWarnings("unchecked")
  void testWithShippingInstructionID() {
    String shippingInstructionID = UUID.randomUUID().toString();
    reference.setShippingInstructionID(shippingInstructionID);

    when(referenceRepository.saveAll(any(Flux.class))).thenReturn(Flux.just(reference));

    ArgumentCaptor<Flux<Reference>> argumentCaptor = ArgumentCaptor.forClass(Flux.class);

    StepVerifier.create(
            referenceService.createReferencesByShippingInstructionIDAndTOs(
                shippingInstructionID, Collections.singletonList(referenceTO)))
        .assertNext(
            referenceTOS -> {
              verify(referenceRepository).saveAll(any(Flux.class));
              assertTrue(referenceTOS.isPresent());
              assertNotNull(referenceTOS.get().get(0).getReferenceValue());
              assertNotNull(referenceTOS.get().get(0).getReferenceValue());
              verify(referenceRepository).saveAll(argumentCaptor.capture());

              Reference capturedArgument = Objects.requireNonNull(argumentCaptor.getValue().blockFirst());
              assertEquals(shippingInstructionID, capturedArgument.getShippingInstructionID());
              assertNull(capturedArgument.getBookingID());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test create reference with shippingInstructionID")
  void testWithoutShippingInstructionID() {
    StepVerifier.create(
            referenceService.createReferencesByShippingInstructionIDAndTOs(null, Collections.singletonList(referenceTO)))
        .expectErrorSatisfies(
                throwable -> {
                Assertions.assertTrue(throwable instanceof CreateException);
                assertEquals("ShippingInstructionID cannot be null", throwable.getMessage());
            })
        .verify();
  }

  @Test
  @DisplayName("Test create reference without bookingID")
  void testWithoutBookingID() {
    StepVerifier.create(
            referenceService.createReferencesByBookingIDAndTOs(null, Collections.singletonList(referenceTO)))
        .expectErrorSatisfies(
                throwable -> {
                Assertions.assertTrue(throwable instanceof CreateException);
                assertEquals("BookingID cannot be null", throwable.getMessage());
            })
        .verify();
  }
}
