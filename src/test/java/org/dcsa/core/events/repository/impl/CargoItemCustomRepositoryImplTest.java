package org.dcsa.core.events.repository.impl;

import org.dcsa.core.events.repository.CargoItemCustomRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.r2dbc.dialect.PostgresDialect;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.r2dbc.core.DatabaseClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for custom CargoItem repository implementation")
public class CargoItemCustomRepositoryImplTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  DatabaseClient client;

  @Spy R2dbcDialect r2dbcDialect = new PostgresDialect();

  @InjectMocks CargoItemCustomRepositoryImpl cargoItemCustomRepository;

  @Test
  @DisplayName(
      "Test fetch all cargo items with cargolineitems for a utilizedTransportEquipmentID should generate correct query")
  void testCargoItemCustomRepositoryQuery() {
    ArgumentCaptor<String> queryCaptor = ArgumentCaptor.forClass(String.class);
    UUID utilizedTransportEquipmentID = UUID.randomUUID();
    cargoItemCustomRepository.findAllCargoItemsAndCargoLineItemsByUtilizedTransportEquipmentID(
        utilizedTransportEquipmentID);
    verify(client).sql(queryCaptor.capture());
    String executedQuery = queryCaptor.getValue();
    Assertions.assertNotNull(executedQuery);
    String expectedQuery =
        "SELECT cargo_item.id, cargo_item.weight, "
            + "cargo_item.volume, cargo_item.weight_unit, cargo_item.volume_unit, cargo_item.number_of_packages, "
            + "cargo_item.package_code, cargo_item.utilized_transport_equipment_id, "
            + "utilized_transport_equipment.id, "
            + "cargo_line_item.cargo_line_item_id, "
            + "cargo_line_item.cargo_item_id, cargo_line_item.shipping_marks, cargo_line_item.id "
            + "FROM cargo_item "
            + "JOIN cargo_line_item ON cargo_line_item.cargo_item_id = cargo_item.id "
            + "JOIN utilized_transport_equipment ON cargo_item.utilized_transport_equipment_id = utilized_transport_equipment.id "
            + "WHERE cargo_item.utilized_transport_equipment_id = '"
            + utilizedTransportEquipmentID
            + "'";
    Assertions.assertEquals(expectedQuery, executedQuery);
  }

  @Test
  @DisplayName("Test fetch cargo items without utilizedTransportEquipment should fail")
  void testCargoItemCustomRepositoryQueryWithoutUtilizedTransportEquipmentID() {
    Exception exception =
        assertThrows(
            NullPointerException.class,
            () -> {
              cargoItemCustomRepository.findAllCargoItemsAndCargoLineItemsByUtilizedTransportEquipmentID(
                  null);
            });

    String expectedMessage = "utilizedTransportEquipmentID must not be null";

    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  @DisplayName(
      "test resultset mapping with all fields present should return CargoItem with CargoLineItems")
  void testMapResultSetWithAllFieldsPresent() {
    Map<String, Object> cargoItemWithCargoLineItem1 = new HashMap<>();
    UUID cargoItemId = UUID.randomUUID();
    UUID utilizedTransportEquipmentID = UUID.randomUUID();
    String shippingInstructionReference = UUID.randomUUID().toString();

    cargoItemWithCargoLineItem1.put("id", cargoItemId);
    cargoItemWithCargoLineItem1.put("description_of_goods", "description of goods");
    cargoItemWithCargoLineItem1.put("hs_code", "720711");
    cargoItemWithCargoLineItem1.put("weight", 100F);
    cargoItemWithCargoLineItem1.put("volume", 300F);
    cargoItemWithCargoLineItem1.put("weight_unit", "KGM");
    cargoItemWithCargoLineItem1.put("volume_unit", "CBM");
    cargoItemWithCargoLineItem1.put("number_of_packages", 2);
    cargoItemWithCargoLineItem1.put("shipping_instruction_id", shippingInstructionReference);
    cargoItemWithCargoLineItem1.put("package_code", "123");
    cargoItemWithCargoLineItem1.put("utilized_transport_equipment_id", utilizedTransportEquipmentID);
    cargoItemWithCargoLineItem1.put("cargo_line_item_id", "1");
    cargoItemWithCargoLineItem1.put("cargo_item_id", cargoItemId);
    cargoItemWithCargoLineItem1.put("shipping_marks", "shipping_marks");

    Map<String, Object> cargoItemWithCargoLineItem2 = new HashMap<>();
    cargoItemWithCargoLineItem2.put("id", cargoItemId);
    cargoItemWithCargoLineItem2.put("description_of_goods", "description of goods");
    cargoItemWithCargoLineItem2.put("hs_code", "720711");
    cargoItemWithCargoLineItem2.put("weight", 100F);
    cargoItemWithCargoLineItem2.put("volume", 300F);
    cargoItemWithCargoLineItem2.put("weight_unit", "KGM");
    cargoItemWithCargoLineItem2.put("volume_unit", "CBM");
    cargoItemWithCargoLineItem2.put("number_of_packages", 2);
    cargoItemWithCargoLineItem2.put("shipping_instruction_id", shippingInstructionReference);
    cargoItemWithCargoLineItem2.put("package_code", "123");
    cargoItemWithCargoLineItem2.put("utilized_transport_equipment_id", utilizedTransportEquipmentID);
    cargoItemWithCargoLineItem2.put("cargo_line_item_id", "2");
    cargoItemWithCargoLineItem2.put("cargo_item_id", cargoItemId);
    cargoItemWithCargoLineItem2.put("shipping_marks", "shipping_marks2");

    List<Map<String, Object>> cargoItemWithCargoLineItems =
        List.of(cargoItemWithCargoLineItem1, cargoItemWithCargoLineItem2);

    CargoItemCustomRepository.CargoItemWithCargoLineItems cargoItemsResult =
        cargoItemCustomRepository.mapResultSet(cargoItemWithCargoLineItems);
    assertEquals(utilizedTransportEquipmentID, cargoItemsResult.getUtilizedTransportEquipmentID());

    // assertions on cargoLineItems
    assertEquals(2, cargoItemsResult.getCargoLineItems().size());
    assertEquals("1", cargoItemsResult.getCargoLineItems().get(0).getCargoLineItemID());
    assertEquals("2", cargoItemsResult.getCargoLineItems().get(1).getCargoLineItemID());
    assertEquals(cargoItemId, cargoItemsResult.getCargoLineItems().get(0).getCargoItemID());
    assertEquals(cargoItemId, cargoItemsResult.getCargoLineItems().get(1).getCargoItemID());
    assertEquals("shipping_marks2", cargoItemsResult.getCargoLineItems().get(1).getShippingMarks());
  }

  @Test
  @DisplayName(
      "test resultset mapping with all fields present should return CargoItem with CargoLineItems")
  void testMapResultSetWithoutCargoLineItems() {
    Map<String, Object> cargoItemWithCargoLineItem1 = new HashMap<>();
    UUID cargoItemId = UUID.randomUUID();
    UUID utilizedTransportEquipmentID = UUID.randomUUID();
    String shippingInstructionReference = UUID.randomUUID().toString();

    cargoItemWithCargoLineItem1.put("id", cargoItemId);
    cargoItemWithCargoLineItem1.put("description_of_goods", "description of goods");
    cargoItemWithCargoLineItem1.put("hs_code", "720711");
    cargoItemWithCargoLineItem1.put("weight", 100F);
    cargoItemWithCargoLineItem1.put("volume", 300F);
    cargoItemWithCargoLineItem1.put("weight_unit", "KGM");
    cargoItemWithCargoLineItem1.put("volume_unit", "CBM");
    cargoItemWithCargoLineItem1.put("number_of_packages", 2);
    cargoItemWithCargoLineItem1.put("shipping_instruction_id", shippingInstructionReference);
    cargoItemWithCargoLineItem1.put("package_code", "123");
    cargoItemWithCargoLineItem1.put("utilized_transport_equipment_id", utilizedTransportEquipmentID);

    List<Map<String, Object>> cargoItemWithCargoLineItems = List.of(cargoItemWithCargoLineItem1);

    CargoItemCustomRepository.CargoItemWithCargoLineItems cargoItemsResult =
        cargoItemCustomRepository.mapResultSet(cargoItemWithCargoLineItems);
    assertEquals(utilizedTransportEquipmentID, cargoItemsResult.getUtilizedTransportEquipmentID());

    // assertions on cargoLineItems
    assertEquals(0, cargoItemsResult.getCargoLineItems().size());
  }

  @Test
  @DisplayName(
      "test resultset mapping with all fields present should return CargoItem with CargoLineItems")
  void testMapResultSetWithOnlyMandatoryFieldsPresent() {
    Map<String, Object> cargoItemWithCargoLineItem1 = new HashMap<>();
    UUID cargoItemId = UUID.randomUUID();
    UUID utilizedTransportEquipmentID = UUID.randomUUID();

    cargoItemWithCargoLineItem1.put("id", cargoItemId);
    cargoItemWithCargoLineItem1.put("description_of_goods", "description of goods");
    cargoItemWithCargoLineItem1.put("hs_code", "720711");
    cargoItemWithCargoLineItem1.put("utilized_transport_equipment_id", utilizedTransportEquipmentID);
    cargoItemWithCargoLineItem1.put("cargo_item_id", cargoItemId);

    Map<String, Object> cargoItemWithCargoLineItem2 = new HashMap<>();
    cargoItemWithCargoLineItem2.put("id", cargoItemId);
    cargoItemWithCargoLineItem2.put("description_of_goods", "description of goods");
    cargoItemWithCargoLineItem2.put("hs_code", "720711");
    cargoItemWithCargoLineItem2.put("utilized_transport_equipment_id", utilizedTransportEquipmentID);
    cargoItemWithCargoLineItem2.put("cargo_item_id", cargoItemId);

    List<Map<String, Object>> cargoItemWithCargoLineItems =
        List.of(cargoItemWithCargoLineItem1, cargoItemWithCargoLineItem2);

    CargoItemCustomRepository.CargoItemWithCargoLineItems cargoItemsResult =
        cargoItemCustomRepository.mapResultSet(cargoItemWithCargoLineItems);
    assertEquals(utilizedTransportEquipmentID, cargoItemsResult.getUtilizedTransportEquipmentID());

    // assertions on cargoLineItems
    assertEquals(2, cargoItemsResult.getCargoLineItems().size());
    assertEquals(cargoItemId, cargoItemsResult.getCargoLineItems().get(0).getCargoItemID());
    assertEquals(cargoItemId, cargoItemsResult.getCargoLineItems().get(1).getCargoItemID());
  }
}
