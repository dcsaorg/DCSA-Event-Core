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
class UtilizedTransportEquipmentCustomRepositoryImplTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  DatabaseClient client;

  @Spy R2dbcDialect r2dbcDialect = new PostgresDialect();

  @InjectMocks
  UtilizedTransportEquipmentCustomRepositoryImpl utilizedTransportEquipmentRepository;

  @Test
  @DisplayName(
      "Test fetch all UtilizedTransportEquipment with equipment for a shipmentID should generate correct query")
  void testCargoItemCustomRepositoryQuery() {
    ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
    UUID shipmentID = UUID.randomUUID();
    utilizedTransportEquipmentRepository.findUtilizedTransportEquipmentDetailsByShipmentID(shipmentID);
    verify(client).sql(queryCaptor.capture());
    String executedQuery = queryCaptor.getValue();
    Assertions.assertNotNull(executedQuery);
    String expectedQuery =
        "SELECT equipment.equipment_reference, utilized_transport_equipment.cargo_gross_weight_unit, utilized_transport_equipment.shipment_id AS shipmentId, "
            + "utilized_transport_equipment.cargo_gross_weight, shipment.id AS sShipmentId, utilized_transport_equipment.is_shipper_owned, "
            + "equipment.iso_equipment_code, utilized_transport_equipment.id, utilized_transport_equipment.equipment_reference, "
            + "equipment.tare_weight, shipment.carrier_booking_reference, equipment.weight_unit FROM utilized_transport_equipment "
            + "JOIN equipment ON utilized_transport_equipment.equipment_reference = equipment.equipment_reference "
            + "JOIN shipment ON utilized_transport_equipment.shipment_id = shipment.id "
            + "WHERE utilized_transport_equipment.shipment_id = '"
            + shipmentID
            + "'";
    Assertions.assertEquals(expectedQuery, executedQuery);
  }
}
