package org.dcsa.core.events.edocumentation.service.impl;

import org.dcsa.core.events.edocumentation.model.mapper.ChargeMapper;
import org.dcsa.core.events.edocumentation.repository.ChargeRepository;
import org.dcsa.core.events.model.Charge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for Charge Service implementation")
class ChargeServiceImplTest {

  @Mock ChargeRepository chargeRepository;
  @Spy ChargeMapper chargeMapper = Mappers.getMapper(ChargeMapper.class);

  @InjectMocks ChargeServiceImpl chargeService;

  Charge charge;

  @BeforeEach
  void init() {
    charge = new Charge();
    charge.setChargeType("chargeTypeCode");
    charge.setTransportDocumentReference("TransportDocumentReference1");
    charge.setShipmentID(UUID.randomUUID());
    charge.setCalculationBasis("calculationBasics");
    charge.setCurrencyCode("EUR");
    charge.setCurrencyAmount(100.5);
    charge.setId("ChargeID");
    charge.setQuantity(100.0);
    charge.setUnitPrice(1.0);
  }

  @Test
  @DisplayName("Test fetch charges for transport document with one charge should return one charge")
  void testFetchSingleChargeByTransportDocument() {
    when(chargeRepository.findAllByTransportDocumentReference(any())).thenReturn(Flux.just(charge));

    StepVerifier.create(
            chargeService.fetchChargesByTransportDocumentReference("TransportDocumentReference1"))
        .assertNext(
            chargeTO -> {
              assertEquals(charge.getChargeType(), chargeTO.getChargeType());
              assertEquals(charge.getQuantity(), chargeTO.getQuantity());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetch multiple charges for transport document should return multiple charges")
  void testFetchMultipleChargesByTransportDocument() {
    Charge charge2 = new Charge();
    charge2.setChargeType("chargeTypeCode2");
    charge2.setTransportDocumentReference("TransportDocumentReference1");
    charge2.setShipmentID(UUID.randomUUID());
    charge2.setCalculationBasis("calculationBasics");
    charge2.setCurrencyCode("EUR");
    charge2.setCurrencyAmount(7600.0);
    charge2.setId("ChargeID2");
    charge2.setQuantity(100.0);
    charge2.setUnitPrice(76.0);

    when(chargeRepository.findAllByTransportDocumentReference(any()))
        .thenReturn(Flux.just(charge, charge2));

    StepVerifier.create(
            chargeService.fetchChargesByTransportDocumentReference("TransportDocumentReference1"))
        .assertNext(
            chargeTO -> {
              assertEquals(charge.getChargeType(), chargeTO.getChargeType());
              assertEquals(charge.getQuantity(), chargeTO.getQuantity());
            })
        .assertNext(
            chargeTO -> {
              assertEquals(charge2.getChargeType(), chargeTO.getChargeType());
              assertEquals(charge2.getQuantity(), chargeTO.getQuantity());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetch no charges found by transportReference should return empty Flux")
  void testFetchNoChargesFoundByTransportReference() {
    when(chargeRepository.findAllByTransportDocumentReference(any())).thenReturn(Flux.empty());

    StepVerifier.create(
            chargeService.fetchChargesByTransportDocumentReference("TransportDocumentReference1"))
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetch single charge by shipmentID")
  void testFetchSingleChargeByShipmentID() {
    when(chargeRepository.findAllByShipmentID(any())).thenReturn(Flux.just(charge));

    StepVerifier.create(chargeService.fetchChargesByShipmentID(UUID.randomUUID()))
        .assertNext(
            chargeTO -> {
              assertEquals(charge.getChargeType(), chargeTO.getChargeType());
              assertEquals(charge.getCalculationBasis(), chargeTO.getCalculationBasis());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetch multiple charges by shipmentID should return multiple charges")
  void testFetchMultipleChargesByShipmentID() {
    Charge charge2 = new Charge();
    charge2.setChargeType("chargeTypeCode2");
    charge2.setTransportDocumentReference("TransportDocumentReference1");
    charge2.setShipmentID(UUID.randomUUID());
    charge2.setCalculationBasis("calculationBasics");
    charge2.setCurrencyCode("EUR");
    charge2.setCurrencyAmount(7600.0);
    charge2.setId("ChargeID2");
    charge2.setQuantity(100.0);
    charge2.setUnitPrice(76.0);

    when(chargeRepository.findAllByShipmentID(any())).thenReturn(Flux.just(charge, charge2));

    StepVerifier.create(chargeService.fetchChargesByShipmentID(UUID.randomUUID()))
        .assertNext(
            chargeTO -> {
              assertEquals(charge.getChargeType(), chargeTO.getChargeType());
              assertEquals(charge.getUnitPrice(), chargeTO.getUnitPrice());
            })
        .assertNext(
            chargeTO -> {
              assertEquals(charge2.getChargeType(), chargeTO.getChargeType());
              assertEquals(charge2.getCurrencyAmount(), chargeTO.getCurrencyAmount());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test no charges found for shipmentID should return empty Flux")
  void testNoChargesFoundByShipmentID() {
    when(chargeRepository.findAllByShipmentID(any())).thenReturn(Flux.empty());

    StepVerifier.create(chargeService.fetchChargesByShipmentID(UUID.randomUUID())).verifyComplete();
  }
}
