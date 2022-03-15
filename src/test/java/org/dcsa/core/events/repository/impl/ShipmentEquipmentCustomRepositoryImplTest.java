package org.dcsa.core.events.repository.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.r2dbc.dialect.PostgresDialect;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.r2dbc.core.DatabaseClient;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for custom Shipment equipment repository implementation")
class ShipmentEquipmentCustomRepositoryImplTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  DatabaseClient client;

  @Spy R2dbcDialect r2dbcDialect = new PostgresDialect();

  @InjectMocks ShipmentEquipmentCustomRepositoryImpl shipmentEquipmentCustomRepository;

  @Test
  @DisplayName(
      "Test fetch all ShipmentEquipment with equipment for a shipmentID should generate correct query")
  void testCargoItemCustomRepositoryQuery() {
    ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
    UUID shipmentID = UUID.randomUUID();
    shipmentEquipmentCustomRepository.findShipmentEquipmentDetailsByShipmentID(shipmentID);
    verify(client).sql(queryCaptor.capture());
    String executedQuery = queryCaptor.getValue();
    Assertions.assertNotNull(executedQuery);
    String expectedQuery =
        "SELECT equipment.equipment_reference, shipment_equipment.cargo_gross_weight_unit, shipment_equipment.shipment_id, "
            + "shipment_equipment.cargo_gross_weight, shipment.shipment_id, shipment_equipment.is_shipper_owned, "
            + "equipment.iso_equipment_code, shipment_equipment.id, shipment_equipment.equipment_reference, "
            + "equipment.tare_weight, shipment.carrier_booking_reference, equipment.weight_unit FROM shipment_equipment "
            + "JOIN equipment ON shipment_equipment.equipment_reference = equipment.equipment_reference "
            + "JOIN shipment ON shipment_equipment.shipment_id = shipment.shipment_id "
            + "WHERE shipment_equipment.shipment_id = '"
            + shipmentID
            + "'";
    Assertions.assertEquals(expectedQuery, executedQuery);
  }
}
