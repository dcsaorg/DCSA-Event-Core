package org.dcsa.core.events.edocumentation.service.impl;

import org.dcsa.core.events.edocumentation.model.mapper.TransportMapper;
import org.dcsa.core.events.edocumentation.repository.ShipmentTransportRepository;
import org.dcsa.core.events.model.*;
import org.dcsa.core.events.model.enums.DCSATransportType;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.events.model.enums.TransportEventTypeCode;
import org.dcsa.core.events.model.enums.TransportPlanStageCode;
import org.dcsa.core.events.repository.*;
import org.dcsa.skernel.model.Vessel;
import org.dcsa.skernel.model.enums.FacilityTypeCode;
import org.dcsa.skernel.model.transferobjects.LocationTO;
import org.dcsa.skernel.repositority.VesselRepository;
import org.dcsa.skernel.service.LocationService;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test for TransportService implementation")
class TransportServiceImplTest {

  // mappers
  @Spy TransportMapper transportMapper = Mappers.getMapper(TransportMapper.class);

  // repositories
  @Mock TransportRepository transportRepository;
  @Mock TransportEventRepository transportEventRepository;
  @Mock TransportCallRepository transportCallRepository;
  @Mock ModeOfTransportRepository modeOfTransportRepository;
  @Mock VesselRepository vesselRepository;
  @Mock VoyageRepository voyageRepository;
  @Mock ShipmentTransportRepository shipmentTransportRepository;

  // services
  @Mock LocationService locationService;

  @InjectMocks TransportServiceImpl transportService;

  Transport transport;
  TransportEvent transportEvent1;
  TransportEvent transportEvent2;

  TransportCall transportCall;

  LocationTO locationTO;
  ModeOfTransport modeOfTransport;
  Vessel vessel;
  Voyage voyage;
  ShipmentTransport shipmentTransport;

  @BeforeEach
  void init() {
    transport = new Transport();
    transport.setTransportName("JD");
    transport.setLoadTransportCallID(UUID.randomUUID());
    transport.setDischargeTransportCallID(UUID.randomUUID());
    transport.setTransportReference("test");
    transport.setTransportID(UUID.randomUUID());
    transport.setLoadTransportCallID(UUID.randomUUID());

    transportEvent1 = new TransportEvent();
    transportEvent1.setEventType(EventType.TRANSPORT);
    transportEvent1.setEventID(UUID.randomUUID());

    transportEvent2 = new TransportEvent();
    transportEvent1.setEventType(EventType.TRANSPORT);
    transportEvent2.setEventID(UUID.randomUUID());

    transportCall = new TransportCall();
    transportCall.setTransportCallID(UUID.randomUUID());
    transportCall.setFacilityTypeCode(FacilityTypeCode.BOCR);
    transportCall.setVesselID(UUID.randomUUID());
    transportCall.setImportVoyageID(UUID.randomUUID());
    transportCall.setExportVoyageID(UUID.randomUUID());

    locationTO = new LocationTO();
    locationTO.setLocationName("Hamburg");

    modeOfTransport = new ModeOfTransport();
    modeOfTransport.setDcsaTransportType(DCSATransportType.VESSEL);
    modeOfTransport.setName("YOY");

    vessel = new Vessel();
    vessel.setVesselName("JZ");
    vessel.setId(UUID.randomUUID());

    voyage = new Voyage();
    voyage.setId(UUID.randomUUID());
    voyage.setServiceID(UUID.randomUUID());
    voyage.setCarrierVoyageNumber("99PROBLEMS");

    shipmentTransport = new ShipmentTransport();
    shipmentTransport.setTransportID(transport.getTransportID());
    shipmentTransport.setShipmentID(UUID.randomUUID());
    shipmentTransport.setTransportPlanStageCode(TransportPlanStageCode.ONC);
    shipmentTransport.setTransportPlanStageSequenceNumber(123);
  }

  @Test
  @DisplayName("Test fetchTransportEventByTransportId should return valid tuple for given ID.")
  void fetchTransportEventByTransportIDForValidID() {

    when(transportRepository.findById(any(UUID.class))).thenReturn(Mono.just(transport));
    when(transportEventRepository
            .findFirstByTransportCallIDAndEventTypeCodeAndEventClassifierCodeOrderByEventDateTimeDesc(
                any(), eq(TransportEventTypeCode.ARRI), any()))
        .thenReturn(Mono.just(transportEvent1));
    when(transportEventRepository
            .findFirstByTransportCallIDAndEventTypeCodeAndEventClassifierCodeOrderByEventDateTimeDesc(
                any(), eq(TransportEventTypeCode.DEPA), any()))
        .thenReturn(Mono.just(transportEvent2));

    StepVerifier.create(transportService.fetchTransportEventByTransportID(UUID.randomUUID()))
        .assertNext(
            res -> {
              assertEquals(transportEvent1.getEventID(), res.getT1().getEventID());
              assertEquals(transportEvent2.getEventID(), res.getT2().getEventID());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetchTransportCallByID should return empty mono for null ID.")
  void fetchTransportCallByIDForNullID() {
    StepVerifier.create(transportService.fetchTransportCallByID(null))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetchTransportCallByID should return valid transport call for given ID.")
  void fetchTransportCallByIDForValidID() {
    when(transportCallRepository.findById(any(UUID.class))).thenReturn(Mono.just(transportCall));
    StepVerifier.create(transportService.fetchTransportCallByID(UUID.randomUUID()))
        .assertNext(
            res -> {
              assertEquals(transportCall.getTransportCallID(), res.getTransportCallID());
              assertEquals(FacilityTypeCode.BOCR, res.getFacilityTypeCode());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test findByTransportID should return valid TransportTO for valid ID.")
  void findByTransportIDForValidID() {

    when(transportRepository.findAllById(any(List.class))).thenReturn(Flux.just(transport));
    when(transportRepository.findById(any(UUID.class))).thenReturn(Mono.just(transport));
    when(transportCallRepository.findById(any(UUID.class))).thenReturn(Mono.just(transportCall));
    when(transportEventRepository
            .findFirstByTransportCallIDAndEventTypeCodeAndEventClassifierCodeOrderByEventDateTimeDesc(
                any(), eq(TransportEventTypeCode.ARRI), any()))
        .thenReturn(Mono.just(transportEvent1));
    when(transportEventRepository
            .findFirstByTransportCallIDAndEventTypeCodeAndEventClassifierCodeOrderByEventDateTimeDesc(
                any(), eq(TransportEventTypeCode.DEPA), any()))
        .thenReturn(Mono.just(transportEvent2));

    when(locationService.fetchLocationByID(transportCall.getLocationID()))
        .thenReturn(Mono.just(locationTO));

    when(modeOfTransportRepository.findByTransportCallID(any()))
        .thenReturn(Mono.just(modeOfTransport));

    when(vesselRepository.findById(any(UUID.class))).thenReturn(Mono.just(vessel));

    when(voyageRepository.findById(any(UUID.class))).thenReturn(Mono.just(voyage));

    StepVerifier.create(transportService.findByTransportID(UUID.randomUUID()))
        .assertNext(
            result -> {
              assertEquals("JD", result.getTransportName());
              assertEquals(DCSATransportType.VESSEL, result.getModeOfTransport());
              assertEquals("JZ", result.getVesselName());
              assertEquals("Hamburg", result.getLoadLocation().getLocationName());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test findByShipmentID should return valid TransportTO for valid ID.")
  void findByShipmentIDForValidID() {

    when(transportRepository.findAllById(any(List.class))).thenReturn(Flux.just(transport));
    when(transportRepository.findById(any(UUID.class))).thenReturn(Mono.just(transport));
    when(transportCallRepository.findById(any(UUID.class))).thenReturn(Mono.just(transportCall));
    when(shipmentTransportRepository.findAllByShipmentID(any(UUID.class))).thenReturn(Flux.just(shipmentTransport));
    when(transportEventRepository
      .findFirstByTransportCallIDAndEventTypeCodeAndEventClassifierCodeOrderByEventDateTimeDesc(
        any(), eq(TransportEventTypeCode.ARRI), any()))
      .thenReturn(Mono.just(transportEvent1));
    when(transportEventRepository
      .findFirstByTransportCallIDAndEventTypeCodeAndEventClassifierCodeOrderByEventDateTimeDesc(
        any(), eq(TransportEventTypeCode.DEPA), any()))
      .thenReturn(Mono.just(transportEvent2));

    when(locationService.fetchLocationByID(transportCall.getLocationID()))
      .thenReturn(Mono.just(locationTO));

    when(modeOfTransportRepository.findByTransportCallID(any()))
      .thenReturn(Mono.just(modeOfTransport));

    when(vesselRepository.findById(any(UUID.class))).thenReturn(Mono.just(vessel));

    when(voyageRepository.findById(any(UUID.class))).thenReturn(Mono.just(voyage));

    StepVerifier.create(transportService.findByShipmentID(shipmentTransport.getShipmentID()))
      .assertNext(
        result -> {
          assertEquals("JD", result.getTransportName());
          assertEquals(DCSATransportType.VESSEL, result.getModeOfTransport());
          assertEquals("JZ", result.getVesselName());
          assertEquals("Hamburg", result.getLoadLocation().getLocationName());
        })
      .verifyComplete();
  }
}
