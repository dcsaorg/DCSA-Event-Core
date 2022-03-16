package org.dcsa.core.events.repository.impl;

import org.dcsa.core.events.model.enums.ShipmentEventTypeCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.dialect.PostgresDialect;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.r2dbc.core.DatabaseClient;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for CustomShipmentRepository Implementation.")
public class ShipmentCustomRepositoryImplTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  DatabaseClient client;

  @Spy R2dbcDialect r2dbcDialect = new PostgresDialect();

  @InjectMocks ShipmentCustomRepositoryImpl shipmentCustomRepository;

  @Test
  void testShipmentCustomRespositoryWithDocumentStatusNoSort() {
    ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
    PageRequest pageRequest = PageRequest.of(0, 10);

    shipmentCustomRepository.findShipmentsAndBookingsByDocumentStatus(
        ShipmentEventTypeCode.RECE, pageRequest);
    verify(client).sql(queryCaptor.capture());
    String executedQuery = queryCaptor.getValue();
    Assertions.assertNotNull(executedQuery);
    String expectedQuery =
        "SELECT booking.carrier_booking_request_reference, shipment.confirmation_datetime, booking.id, booking.document_status, "
            + "shipment.id, shipment.booking_id, shipment.carrier_booking_reference, shipment.terms_and_conditions, shipment.updated_date_time "
            + "FROM shipment "
            + "JOIN booking ON shipment.booking_id = booking.id "
            + "WHERE booking.document_status = 'RECE' LIMIT 10 OFFSET 0";
    Assertions.assertEquals(expectedQuery, executedQuery);
  }

  @Test
  void testShipmentCustomRespositoryWithDocumentStatusAndSort() {
    ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
    PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "confirmationDateTime"));

    shipmentCustomRepository.findShipmentsAndBookingsByDocumentStatus(
      ShipmentEventTypeCode.PENU, pageRequest);
    verify(client).sql(queryCaptor.capture());
    String executedQuery = queryCaptor.getValue();
    Assertions.assertNotNull(executedQuery);
    String expectedQuery =
      "SELECT booking.carrier_booking_request_reference, shipment.confirmation_datetime, booking.id, booking.document_status, " +
        "shipment.id, shipment.booking_id, shipment.carrier_booking_reference, shipment.terms_and_conditions, shipment.updated_date_time " +
        "FROM shipment " +
        "JOIN booking ON shipment.booking_id = booking.id " +
        "WHERE booking.document_status = 'PENU' " +
        "ORDER BY shipment.confirmation_datetime DESC LIMIT 10 OFFSET 0";
    Assertions.assertEquals(expectedQuery, executedQuery);
  }

  @Test
  void testShipmentCustomRespositoryWithDocumentStatusAndMultipleSort() {
    ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
    PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("confirmationDateTime"), Sort.Order.desc("documentStatus")));

    shipmentCustomRepository.findShipmentsAndBookingsByDocumentStatus(
      ShipmentEventTypeCode.PENU, pageRequest);
    verify(client).sql(queryCaptor.capture());
    String executedQuery = queryCaptor.getValue();
    Assertions.assertNotNull(executedQuery);
    String expectedQuery =
      "SELECT booking.carrier_booking_request_reference, shipment.confirmation_datetime, booking.id, booking.document_status, " +
        "shipment.id, shipment.booking_id, shipment.carrier_booking_reference, shipment.terms_and_conditions, shipment.updated_date_time " +
        "FROM shipment " +
        "JOIN booking ON shipment.booking_id = booking.id " +
        "WHERE booking.document_status = 'PENU' " +
        "ORDER BY booking.document_status DESC, shipment.confirmation_datetime ASC LIMIT 10 OFFSET 0";
    Assertions.assertEquals(expectedQuery, executedQuery);
  }

  @Test
  void testShipmentCustomRespositoryWithoutDocumentStatusAndMultipleSort() {
    ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
    PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("confirmationDateTime"), Sort.Order.desc("documentStatus")));

    shipmentCustomRepository.findShipmentsAndBookingsByDocumentStatus(
      null, pageRequest);
    verify(client).sql(queryCaptor.capture());
    String executedQuery = queryCaptor.getValue();
    Assertions.assertNotNull(executedQuery);
    String expectedQuery =
      "SELECT booking.carrier_booking_request_reference, shipment.confirmation_datetime, booking.id, booking.document_status, " +
        "shipment.id, shipment.booking_id, shipment.carrier_booking_reference, shipment.terms_and_conditions, shipment.updated_date_time " +
        "FROM shipment " +
        "JOIN booking ON shipment.booking_id = booking.id " +
        "WHERE NULL IS NULL " +
        "ORDER BY booking.document_status DESC, shipment.confirmation_datetime ASC LIMIT 10 OFFSET 0";
    Assertions.assertEquals(expectedQuery, executedQuery);
  }
}
