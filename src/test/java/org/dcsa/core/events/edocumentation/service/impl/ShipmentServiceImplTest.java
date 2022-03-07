package org.dcsa.core.events.edocumentation.service.impl;

import org.dcsa.core.events.edocumentation.model.mapper.CarrierClauseMapper;
import org.dcsa.core.events.edocumentation.model.mapper.ChargeMapper;
import org.dcsa.core.events.edocumentation.model.mapper.ConfirmedEquipmentMapper;
import org.dcsa.core.events.edocumentation.model.mapper.ShipmentMapper;
import org.dcsa.core.events.edocumentation.model.transferobject.CarrierClauseTO;
import org.dcsa.core.events.edocumentation.model.transferobject.TransportTO;
import org.dcsa.core.events.edocumentation.repository.*;
import org.dcsa.core.events.edocumentation.service.BookingService;
import org.dcsa.core.events.edocumentation.service.CarrierClauseService;
import org.dcsa.core.events.edocumentation.service.TransportService;
import org.dcsa.core.events.model.*;
import org.dcsa.core.events.model.enums.*;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.events.repository.ShipmentRepository;
import org.dcsa.core.events.service.LocationService;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test for ShipmentService implementation.")
class ShipmentServiceImplTest {

  // mappers
  @Spy ShipmentMapper shipmentMapper = Mappers.getMapper(ShipmentMapper.class);
  @Spy CarrierClauseMapper carrierClauseMapper = Mappers.getMapper(CarrierClauseMapper.class);

  @Spy
  ConfirmedEquipmentMapper confirmedEquipmentMapper =
      Mappers.getMapper(ConfirmedEquipmentMapper.class);

  @Spy ChargeMapper chargeMapper = Mappers.getMapper(ChargeMapper.class);

  // services
  @Mock LocationService locationService;
  @Mock TransportService transportService;
  @Mock BookingService bookingService;
  @Mock CarrierClauseService carrierClauseService;

  // repos
  @Mock ShipmentRepository shipmentRepository;
  @Mock ShipmentCutOffTimeRepository shipmentCutOffTimeRepository;
  @Mock ShipmentLocationRepository shipmentLocationRepository;
  @Mock RequestedEquipmentRepository requestedEquipmentRepository;
  @Mock ChargeRepository chargeRepository;
  @Mock ShipmentTransportRepository shipmentTransportRepository;

  @InjectMocks ShipmentServiceImpl shipmentService;

  // models
  ShipmentCutOffTime shipmentCutOffTime;
  LocationTO locationTO;
  ShipmentLocation shipmentLocation;
  ShipmentCarrierClause shipmentCarrierClause;
  RequestedEquipment requestedEquipment;
  Charge charge;
  ShipmentTransport shipmentTransport;
  TransportTO transportTO;
  CarrierClauseTO carrierClauseTO;
  Shipment shipment;

  @BeforeEach
  void init() {
    shipmentCutOffTime = new ShipmentCutOffTime();
    shipmentCutOffTime.setShipmentID(UUID.randomUUID());
    shipmentCutOffTime.setCutOffDateTimeCode(CutOffDateTimeCode.AFD);
    shipmentCutOffTime.setCutOffDateTime(OffsetDateTime.now());

    locationTO = new LocationTO();
    locationTO.setLocationName("Hamburg");

    shipmentLocation = new ShipmentLocation();
    shipmentLocation.setShipmentID(UUID.randomUUID());
    shipmentLocation.setLocationID(UUID.randomUUID().toString());
    shipmentLocation.setDisplayedName("Tokyo");
    shipmentLocation.setShipmentLocationTypeCode(LocationType.DRL);

    shipmentCarrierClause = new ShipmentCarrierClause();
    shipmentCarrierClause.setCarrierClauseID(UUID.randomUUID());
    shipmentCarrierClause.setTransportDocumentReference("ref");

    carrierClauseTO = new CarrierClauseTO();
    carrierClauseTO.setClauseContent("clause content");

    requestedEquipment = new RequestedEquipment();
    requestedEquipment.setBookingID(UUID.randomUUID());
    requestedEquipment.setConfirmedEquipmentSizetype("22GP");
    requestedEquipment.setConfirmedEquipmentUnits(3);

    charge = new Charge();
    charge.setChargeType("x".repeat(20));
    charge.setId(UUID.randomUUID().toString());
    charge.setShipmentID(UUID.randomUUID());
    charge.setCalculationBasis("WHAT");
    charge.setCurrencyAmount(12.12);
    charge.setCurrencyCode("x".repeat(20));
    charge.setPaymentTermCode(PaymentTerm.PRE);
    charge.setQuantity(123d);
    charge.setTransportDocumentReference("x".repeat(20));
    charge.setUnitPrice(12.12d);

    shipmentTransport = new ShipmentTransport();
    shipmentTransport.setTransportID(UUID.randomUUID());
    shipmentTransport.setTransportPlanStageCode(TransportPlanStageCode.MNC);
    shipmentTransport.setTransportPlanStageSequenceNumber(33);
    shipmentTransport.setIsUnderShippersResponsibility(true);

    transportTO = new TransportTO();
    transportTO.setTransportName("YOYO");
    transportTO.setModeOfTransport(DCSATransportType.VESSEL);

    shipment = new Shipment();
    shipment.setShipmentID(UUID.randomUUID());
    shipment.setBookingID(UUID.randomUUID());
    shipment.setCarrierID(UUID.randomUUID());
    shipment.setCarrierBookingReference(UUID.randomUUID().toString());
    shipment.setTermsAndConditions("Terms and conditions etc...");
    shipment.setConfirmationDateTime(OffsetDateTime.now());
  }

  @Test
  @DisplayName("Test ShipmentCutOffTimeByShipmentID should return empty list if ID is null.")
  void fetchShipmentCutOffTimeByShipmentIDForNullID() {

    StepVerifier.create(shipmentService.fetchShipmentCutOffTimeByShipmentID(null))
        .assertNext(
            res -> {
              assertEquals(0, res.size());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName(
      "Test ShipmentCutOffTimeByShipmentID should return empty list if no results returned.")
  void fetchShipmentCutOffTimeByShipmentIDForValidIDButEmptyRes() {

    when(shipmentCutOffTimeRepository.findAllByShipmentID(any())).thenReturn(Flux.empty());

    StepVerifier.create(shipmentService.fetchShipmentCutOffTimeByShipmentID(UUID.randomUUID()))
        .assertNext(
            res -> {
              assertEquals(0, res.size());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test ShipmentCutOffTimeByShipmentID should return valid result for valid ID")
  void fetchShipmentCutOffTimeByShipmentIDForValidID() {

    when(shipmentCutOffTimeRepository.findAllByShipmentID(any()))
        .thenReturn(Flux.just(shipmentCutOffTime));

    StepVerifier.create(shipmentService.fetchShipmentCutOffTimeByShipmentID(UUID.randomUUID()))
        .assertNext(
            result -> {
              assertEquals(1, result.size());
              assertEquals(
                  shipmentCutOffTime.getCutOffDateTimeCode(),
                  result.get(0).getCutOffDateTimeCode());
              assertEquals(
                  shipmentCutOffTime.getCutOffDateTime(), result.get(0).getCutOffDateTime());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetchShipmentLocationsByBookingID should return empty list if ID is null.")
  void fetchShipmentLocationsByBookingIDForNullID() {

    StepVerifier.create(shipmentService.fetchShipmentLocationsByBookingID(null))
        .assertNext(
            res -> {
              assertEquals(0, res.size());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName(
      "Test fetchShipmentLocationsByBookingID should return empty list if no results returned.")
  void fetchShipmentLocationsByBookingIDForValidIDButEmptyRes() {

    when(shipmentLocationRepository.findByBookingID(any())).thenReturn(Flux.empty());

    StepVerifier.create(shipmentService.fetchShipmentLocationsByBookingID(UUID.randomUUID()))
        .assertNext(
            res -> {
              assertEquals(0, res.size());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetchShipmentLocationsByBookingID should return valid result for valid IDd.")
  void fetchShipmentLocationsByBookingIDForValidID() {

    when(shipmentLocationRepository.findByBookingID(any())).thenReturn(Flux.just(shipmentLocation));
    when(locationService.fetchLocationByID(any())).thenReturn(Mono.just(locationTO));

    StepVerifier.create(shipmentService.fetchShipmentLocationsByBookingID(UUID.randomUUID()))
        .assertNext(
            res -> {
              assertEquals(1, res.size());
              assertEquals("Hamburg", res.get(0).getLocation().getLocationName());
              assertEquals("Tokyo", res.get(0).getDisplayedName());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetchCarrierClausesByShipmentID should return empty list if ID is null.")
  void fetchCarrierClausesByShipmentIDForNullID() {

    StepVerifier.create(shipmentService.fetchCarrierClausesByShipmentID(null))
        .verifyComplete();
  }

  @Test
  @DisplayName(
      "Test fetchCarrierClausesByShipmentID should return empty list if no results returned.")
  void fetchCarrierClausesByShipmentIDForValidIDButEmptyRes() {

    when(carrierClauseService.fetchCarrierClausesByShipmentID(any())).thenReturn(Flux.empty());

    StepVerifier.create(shipmentService.fetchCarrierClausesByShipmentID(UUID.randomUUID()))
        .assertNext(
            res -> {
              assertEquals(0, res.size());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetchCarrierClausesByShipmentID should return valid result for valid ID.")
  void fetchCarrierClausesByShipmentIDForValidID() {

    when(carrierClauseService.fetchCarrierClausesByShipmentID(any()))
        .thenReturn(Flux.just(carrierClauseTO));

    StepVerifier.create(shipmentService.fetchCarrierClausesByShipmentID(UUID.randomUUID()))
        .assertNext(
            res -> {
              assertEquals(1, res.size());
              assertEquals("clause content", res.get(0).getClauseContent());
            })
        .verifyComplete();
  }

  @DisplayName("Test fetchConfirmedEquipmentByByBookingID should return empty list if ID is null.")
  void fetchConfirmedEquipmentByByBookingIDForNullID() {

    StepVerifier.create(shipmentService.fetchConfirmedEquipmentByByBookingID(null))
        .assertNext(
            res -> {
              assertEquals(0, res.size());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName(
      "Test fetchConfirmedEquipmentByByBookingID should return empty list if no results returned.")
  void fetchConfirmedEquipmentByByBookingIDForValidIDButEmptyRes() {

    when(requestedEquipmentRepository.findByBookingID(any())).thenReturn(Flux.empty());

    StepVerifier.create(shipmentService.fetchConfirmedEquipmentByByBookingID(UUID.randomUUID()))
        .assertNext(
            res -> {
              assertEquals(0, res.size());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetchConfirmedEquipmentByByBookingID should return valid result for valid ID.")
  void fetchConfirmedEquipmentByByBookingIDForValidID() {

    when(requestedEquipmentRepository.findByBookingID(any()))
        .thenReturn(Flux.just(requestedEquipment));

    StepVerifier.create(shipmentService.fetchConfirmedEquipmentByByBookingID(UUID.randomUUID()))
        .assertNext(
            res -> {
              assertEquals(1, res.size());
              assertEquals("22GP", res.get(0).getConfirmedEquipmentSizetype());
              assertEquals(3, res.get(0).getConfirmedEquipmentUnits());
            })
        .verifyComplete();
  }

  @DisplayName("Test fetchChargesByShipmentID should return empty list if ID is null.")
  void fetchChargesByShipmentIDForNullID() {

    StepVerifier.create(shipmentService.fetchChargesByShipmentID(null))
        .assertNext(
            res -> {
              assertEquals(0, res.size());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetchChargesByShipmentID should return empty list if no results returned.")
  void fetchChargesByShipmentIDForValidIDButEmptyRes() {

    when(chargeRepository.findAllByShipmentID(any())).thenReturn(Flux.empty());

    StepVerifier.create(shipmentService.fetchChargesByShipmentID(UUID.randomUUID()))
        .assertNext(
            res -> {
              assertEquals(0, res.size());
            })
        .verifyComplete();
  }

  @DisplayName("Test fetchChargesByShipmentID should return valid result for valid ID.")
  void fetchChargesByShipmentIDForValidID() {

    when(chargeRepository.findAllByShipmentID(any())).thenReturn(Flux.just(charge));

    StepVerifier.create(shipmentService.fetchChargesByShipmentID(UUID.randomUUID()))
        .assertNext(
            res -> {
              assertEquals(1, res.size());
              assertEquals(charge.getChargeType(), res.get(0).getChargeType());
              assertEquals("WHAT", res.get(0).getCalculationBasis());
              assertEquals(PaymentTerm.PRE, res.get(0).getPaymentTermCode());
              assertEquals(12.12, res.get(0).getCurrencyAmount());
              assertEquals(123d, res.get(0).getQuantity());
              assertEquals(12.12d, res.get(0).getUnitPrice());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetchTransportsByShipmentID should return empty list if ID is null.")
  void fetchTransportsByShipmentIDForNullID() {

    StepVerifier.create(shipmentService.fetchChargesByShipmentID(null))
        .assertNext(
            res -> {
              assertEquals(0, res.size());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetchTransportsByShipmentID should return empty list if no results returned.")
  void fetchTransportsByShipmentIDForValidIDButEmptyRes() {

    when(shipmentTransportRepository.findAllByShipmentID(any())).thenReturn(Flux.empty());

    StepVerifier.create(shipmentService.fetchTransportsByShipmentID(UUID.randomUUID()))
        .assertNext(
            res -> {
              assertEquals(0, res.size());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetchTransportsByShipmentID should return valid result for valid ID.")
  void fetchTransportsByShipmentIDForValidID() {

    when(shipmentTransportRepository.findAllByShipmentID(any()))
        .thenReturn(Flux.just(shipmentTransport));
    when(transportService.findByTransportID(any())).thenReturn(Flux.just(transportTO));

    StepVerifier.create(shipmentService.fetchTransportsByShipmentID(UUID.randomUUID()))
        .assertNext(
            res -> {
              assertEquals(1, res.size());
              assertEquals("YOYO", res.get(0).getTransportName());
              assertEquals(DCSATransportType.VESSEL, res.get(0).getModeOfTransport());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test findByShippingInstructionReference should return valid shipmentTO for valid ID.")
  void findByShippingInstructionReferenceForValidID() {

    when(shipmentRepository.findByShippingInstructionReference(any())).thenReturn(Flux.just(shipment));

    when(shipmentCutOffTimeRepository.findAllByShipmentID(any()))
        .thenReturn(Flux.just(shipmentCutOffTime));
    when(shipmentLocationRepository.findByBookingID(any())).thenReturn(Flux.just(shipmentLocation));

    when(carrierClauseService.fetchCarrierClausesByShipmentID(any()))
        .thenReturn(Flux.just(carrierClauseTO));
    when(requestedEquipmentRepository.findByBookingID(any()))
        .thenReturn(Flux.just(requestedEquipment));
    when(chargeRepository.findAllByShipmentID(any())).thenReturn(Flux.just(charge));
    when(shipmentTransportRepository.findAllByShipmentID(any()))
        .thenReturn(Flux.just(shipmentTransport));
    when(transportService.findByTransportID(any())).thenReturn(Flux.just(transportTO));
    when(locationService.fetchLocationByID(any())).thenReturn(Mono.just(locationTO));
    when(bookingService.fetchByBookingID(any())).thenReturn(Mono.empty());

    StepVerifier.create(shipmentService.findByShippingInstructionReference(UUID.randomUUID().toString()))
        .assertNext(
            result -> {
              assertEquals(
                  shipmentCutOffTime.getCutOffDateTimeCode(),
                  result.get(0).getShipmentCutOffTimes().get(0).getCutOffDateTimeCode());
              assertEquals(
                  shipmentCutOffTime.getCutOffDateTime(),
                  result.get(0).getShipmentCutOffTimes().get(0).getCutOffDateTime());
              assertEquals(
                  "Hamburg", result.get(0).getShipmentLocations().get(0).getLocation().getLocationName());
              assertEquals("Tokyo", result.get(0).getShipmentLocations().get(0).getDisplayedName());
              assertEquals("clause content", result.get(0).getCarrierClauses().get(0).getClauseContent());
              assertEquals(
                  "22GP", result.get(0).getConfirmedEquipments().get(0).getConfirmedEquipmentSizetype());
              assertEquals(3, result.get(0).getConfirmedEquipments().get(0).getConfirmedEquipmentUnits());
              assertEquals(
                  charge.getChargeType(), result.get(0).getCharges().get(0).getChargeType());
              assertEquals("WHAT", result.get(0).getCharges().get(0).getCalculationBasis());
              assertEquals(PaymentTerm.PRE, result.get(0).getCharges().get(0).getPaymentTermCode());
              assertEquals("YOYO", result.get(0).getTransports().get(0).getTransportName());
              assertEquals(
                  DCSATransportType.VESSEL, result.get(0).getTransports().get(0).getModeOfTransport());
            })
        .verifyComplete();
  }
}
