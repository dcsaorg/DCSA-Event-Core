package org.dcsa.core.events.edocumentation.service.impl;

import org.dcsa.core.events.edocumentation.model.mapper.CarrierClauseMapper;
import org.dcsa.core.events.edocumentation.repository.CarrierClauseRepository;
import org.dcsa.core.events.edocumentation.repository.ShipmentCarrierClausesRepository;
import org.dcsa.core.events.model.CarrierClause;
import org.dcsa.core.events.model.ShipmentCarrierClause;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test for Carrier Clause Service implementation.")
class CarrierClauseServiceImplTest {

  @Mock ShipmentCarrierClausesRepository shipmentCarrierClausesRepository;
  @Mock CarrierClauseRepository carrierClauseRepository;

  @Spy CarrierClauseMapper carrierClauseMapper = Mappers.getMapper(CarrierClauseMapper.class);

  @InjectMocks CarrierClauseServiceImpl carrierClauseService;

  private CarrierClause carrierClause;
  private ShipmentCarrierClause shipmentCarrierClause;
  private UUID shipmentID = UUID.fromString("afbc95b5-35c5-4855-afc2-066b4e91f2a5");

  @BeforeEach
  void init() {
    UUID carrierClauseID = UUID.randomUUID();
    carrierClause = new CarrierClause();
    carrierClause.setClauseContent("Carrier Clause1");
    carrierClause.setId(carrierClauseID);

    shipmentCarrierClause = new ShipmentCarrierClause();
    shipmentCarrierClause.setCarrierClauseID(carrierClauseID);
    shipmentCarrierClause.setShipmentID(shipmentID);
    shipmentCarrierClause.setTransportDocumentReference("transportDocumentReference1");
  }

  @Test
  @DisplayName(
      "Find carrier clause for transport document with one carrier clause should return one carrier clause")
  void fetchSingleCarrierClausesByTransportDocumentReference() {
    when(shipmentCarrierClausesRepository.findAllByTransportDocumentReference(
            eq("transportDocumentReference1")))
        .thenReturn(Flux.just(shipmentCarrierClause));
    when(carrierClauseRepository.findById((UUID) any())).thenReturn(Mono.just(carrierClause));

    StepVerifier.create(
            carrierClauseService.fetchCarrierClausesByTransportDocumentReference(
                "transportDocumentReference1"))
        .assertNext(
            carrierClauseTO -> {
              verify(carrierClauseRepository).findById(eq(carrierClause.getId()));
              assertEquals(carrierClause.getClauseContent(), carrierClauseTO.getClauseContent());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName(
      "Find carrier clauses for transport document with multiple carrier clauses should return multiple carrier clauses")
  void fetchMultipleCarrierClausesByTransportDocumentReference() {
    UUID carrierClauseID = UUID.randomUUID();
    CarrierClause carrierClause2 = new CarrierClause();
    carrierClause2.setId(carrierClauseID);
    carrierClause2.setClauseContent("Carrier Clause2");

    ShipmentCarrierClause shipmentCarrierClause2 = new ShipmentCarrierClause();
    shipmentCarrierClause2.setCarrierClauseID(carrierClauseID);
    shipmentCarrierClause2.setShipmentID(UUID.randomUUID());
    shipmentCarrierClause2.setTransportDocumentReference("transportDocumentReference1");

    when(shipmentCarrierClausesRepository.findAllByTransportDocumentReference(
            eq("transportDocumentReference1")))
        .thenReturn(Flux.just(shipmentCarrierClause, shipmentCarrierClause2));
    when(carrierClauseRepository.findById((UUID) any())).thenReturn(Mono.just(carrierClause));
    when(carrierClauseRepository.findById(eq(carrierClauseID)))
        .thenReturn(Mono.just(carrierClause2));

    StepVerifier.create(
            carrierClauseService.fetchCarrierClausesByTransportDocumentReference(
                "transportDocumentReference1"))
        .assertNext(
            carrierClauseTO -> {
              verify(carrierClauseRepository).findById((UUID) any());
              assertEquals(carrierClause.getClauseContent(), carrierClauseTO.getClauseContent());
            })
        .assertNext(
            carrierClauseTO -> {
              verify(carrierClauseRepository).findById(eq(carrierClauseID));
              assertEquals(carrierClause2.getClauseContent(), carrierClauseTO.getClauseContent());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Transport document without carrier clauses should return no carrier clauses")
  void fetchCarrierClausesByTransportDocumentReferenceNothingFound() {
    when(shipmentCarrierClausesRepository.findAllByTransportDocumentReference(
            eq("transportDocumentReference1")))
        .thenReturn(Flux.empty());

    StepVerifier.create(
            carrierClauseService.fetchCarrierClausesByTransportDocumentReference(
                "transportDocumentReference1"))
        .verifyComplete();
  }

  @Test
  @DisplayName(
      "Transport document with shipmentCarrier clauses without carrier clauses should return no carrier clauses")
  void fetchCarrierClausesByTransportDocumentReferenceNoCarrierClausesFound() {
    when(shipmentCarrierClausesRepository.findAllByTransportDocumentReference(
            eq("transportDocumentReference1")))
        .thenReturn(Flux.just(shipmentCarrierClause));
    when(carrierClauseRepository.findById((UUID) any())).thenReturn(Mono.empty());

    StepVerifier.create(
            carrierClauseService.fetchCarrierClausesByTransportDocumentReference(
                "transportDocumentReference1"))
        .verifyComplete();
  }

  @Test
  @DisplayName(
    "Find carrier clause for shipment with one carrier clause should return one carrier clause")
  void fetchSingleCarrierClausesByShipmentID() {
    when(shipmentCarrierClausesRepository.findAllByShipmentID(
      eq(shipmentID)))
      .thenReturn(Flux.just(shipmentCarrierClause));
    when(carrierClauseRepository.findById((UUID) any())).thenReturn(Mono.just(carrierClause));

    StepVerifier.create(
        carrierClauseService.fetchCarrierClausesByShipmentID(
          shipmentID))
      .assertNext(
        carrierClauseTO -> {
          verify(carrierClauseRepository).findById(eq(carrierClause.getId()));
          assertEquals(carrierClause.getClauseContent(), carrierClauseTO.getClauseContent());
        })
      .verifyComplete();
  }

  @Test
  @DisplayName(
    "Find carrier clauses for shipment with multiple carrier clauses should return multiple carrier clauses")
  void fetchMultipleCarrierClausesByShipmentID() {
    UUID carrierClauseID = UUID.randomUUID();
    CarrierClause carrierClause2 = new CarrierClause();
    carrierClause2.setId(carrierClauseID);
    carrierClause2.setClauseContent("Carrier Clause2");

    ShipmentCarrierClause shipmentCarrierClause2 = new ShipmentCarrierClause();
    shipmentCarrierClause2.setCarrierClauseID(carrierClauseID);
    shipmentCarrierClause2.setShipmentID(shipmentID);
    shipmentCarrierClause2.setTransportDocumentReference("transportDocumentReference1");

    when(shipmentCarrierClausesRepository.findAllByShipmentID(
      eq(shipmentID)))
      .thenReturn(Flux.just(shipmentCarrierClause, shipmentCarrierClause2));
    when(carrierClauseRepository.findById((UUID) any())).thenReturn(Mono.just(carrierClause));
    when(carrierClauseRepository.findById(eq(carrierClauseID)))
      .thenReturn(Mono.just(carrierClause2));

    StepVerifier.create(
        carrierClauseService.fetchCarrierClausesByShipmentID(
          shipmentID))
      .assertNext(
        carrierClauseTO -> {
          verify(carrierClauseRepository).findById((UUID) any());
          assertEquals(carrierClause.getClauseContent(), carrierClauseTO.getClauseContent());
        })
      .assertNext(
        carrierClauseTO -> {
          verify(carrierClauseRepository).findById(eq(carrierClauseID));
          assertEquals(carrierClause2.getClauseContent(), carrierClauseTO.getClauseContent());
        })
      .verifyComplete();
  }

  @Test
  @DisplayName("Shipment without carrier clauses should return no carrier clauses")
  void fetchCarrierClausesByShipmentIDNothingFound() {
    when(shipmentCarrierClausesRepository.findAllByShipmentID(
      eq(shipmentID)))
      .thenReturn(Flux.empty());

    StepVerifier.create(
        carrierClauseService.fetchCarrierClausesByShipmentID(
          shipmentID))
      .verifyComplete();
  }

  @Test
  @DisplayName(
    "Shipment with shipmentCarrier clauses without carrier clauses should return no carrier clauses")
  void fetchCarrierClausesByShipmentIDNoCarrierClausesFound() {
    when(shipmentCarrierClausesRepository.findAllByShipmentID(
      eq(shipmentID)))
      .thenReturn(Flux.just(shipmentCarrierClause));
    when(carrierClauseRepository.findById((UUID) any())).thenReturn(Mono.empty());

    StepVerifier.create(
        carrierClauseService.fetchCarrierClausesByShipmentID(
          shipmentID))
      .verifyComplete();
  }
}
