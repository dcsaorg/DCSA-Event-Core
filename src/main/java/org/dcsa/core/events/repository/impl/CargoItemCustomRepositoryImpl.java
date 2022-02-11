package org.dcsa.core.events.repository.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.CargoLineItem;
import org.dcsa.core.events.model.enums.VolumeUnit;
import org.dcsa.core.events.model.enums.WeightUnit;
import org.dcsa.core.events.repository.CargoItemCustomRepository;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.data.relational.core.dialect.RenderContextFactory;
import org.springframework.data.relational.core.sql.*;
import org.springframework.data.relational.core.sql.render.SqlRenderer;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CargoItemCustomRepositoryImpl implements CargoItemCustomRepository {

  private final R2dbcDialect r2dbcDialect;
  private final DatabaseClient client;

  private static final Table CARGO_ITEM_TABLE = Table.create("cargo_item");
  private static final Table CARGO_LINE_ITEM_TABLE = Table.create("cargo_line_item");
  private static final Table SHIPMENT_TABLE = Table.create("shipment");

  @Override
  public Flux<CargoItemWithCargoLineItems> findAllCargoItemsAndCargoLineItemsByShipmentEquipmentID(
      UUID shipmentEquipmentID) {

    //	Programmatically creates the below query:
    //		SELECT ci.id, ci.shipment_id, ci.description_of_goods, ci.hs_code, ci.weight, ci.volume,
    // ci.weight_unit, ci.volume_unit, ci.number_of_packages, ci.shipping_instruction_id,
    //		ci.package_code, ci.shipment_equipment_id, cli.cargo_line_item_id, cli.shipping_marks,
    // cli.id
    //		FROM dcsa_im_v3_0.cargo_item ci
    //		join dcsa_im_v3_0.cargo_line_item cli on cli.cargo_item_id = ci.id
    //		where ci.shipment_equipment_id = :shipmentEquipmentID;

    Objects.requireNonNull(shipmentEquipmentID, "ShipmentEquiment must not be null");

    Select selectJoin =
        Select.builder()
            .select(queryColumnMap().values())
            .from(CARGO_ITEM_TABLE)
            .join(CARGO_LINE_ITEM_TABLE)
            .on(queryColumnMap().get("cli.cargo_item_id"))
            .equals(queryColumnMap().get("ci.id"))
            .join(SHIPMENT_TABLE)
            .on(queryColumnMap().get("s.shipment_id"))
            .equals(queryColumnMap().get("ci.shipment_id"))
            .where(
                Conditions.isEqual(
                    queryColumnMap().get("ci.shipment_equipment_id"),
                    SQL.literalOf(shipmentEquipmentID)))
            .build();

    RenderContextFactory factory = new RenderContextFactory(r2dbcDialect);
    SqlRenderer sqlRenderer = SqlRenderer.create(factory.createRenderContext());

    return client
        .sql(sqlRenderer.render(selectJoin))
        .fetch()
        .all()
        .bufferUntilChanged(resultSet -> resultSet.get("ci.id"))
        .map(cargoItemResultSet -> mapResultSet(cargoItemResultSet));
  }

  CargoItemWithCargoLineItems mapResultSet(List<Map<String, Object>> cargoItemResultSet) {
    Map<String, Object> cargoItemResult = cargoItemResultSet.get(0);
    List<CargoLineItem> cargoLineItems =
        cargoItemResultSet.stream()
            .filter(
                cargoLineItemResultSet ->
                    Objects.nonNull(cargoLineItemResultSet.get("cli.cargo_item_id")))
            .map(
                cargoLineItemResultSet -> {
                  CargoLineItem cargoLineItem = new CargoLineItem();
                  cargoLineItem.setCargoItemID(
                      (UUID) cargoLineItemResultSet.get("cli.cargo_item_id"));
                  cargoLineItem.setCargoLineItemID(
                      String.valueOf(cargoLineItemResultSet.get("cli.cargo_line_item_id")));
                  cargoLineItem.setShippingMarks(
                      String.valueOf(cargoLineItemResultSet.get("cli.shipping_marks")));
                  return cargoLineItem;
                })
            .collect(Collectors.toList());
    CargoItemWithCargoLineItems cargoItemWithCargoLineItems = new CargoItemWithCargoLineItems();
    cargoItemWithCargoLineItems.setId(
        UUID.fromString(String.valueOf(cargoItemResult.get("ci.id"))));
    cargoItemWithCargoLineItems.setShipmentID(
        UUID.fromString(String.valueOf(cargoItemResult.get("ci.shipment_id"))));
    cargoItemWithCargoLineItems.setDescriptionOfGoods(
        String.valueOf(cargoItemResult.get("ci.description_of_goods")));
    cargoItemWithCargoLineItems.setHsCode(String.valueOf(cargoItemResult.get("ci.hs_code")));
    cargoItemWithCargoLineItems.setWeight((Float) cargoItemResult.get("ci.weight"));

    if (Objects.nonNull(cargoItemResult.get("ci.weight_unit"))) {
      cargoItemWithCargoLineItems.setWeightUnit(
          WeightUnit.valueOf(String.valueOf(cargoItemResult.get("ci.weight_unit"))));
    }

    cargoItemWithCargoLineItems.setVolume((Float) cargoItemResult.get("ci.volume"));

    if (Objects.nonNull(cargoItemResult.get("ci.volume_unit"))) {
      cargoItemWithCargoLineItems.setVolumeUnit(
          VolumeUnit.valueOf(String.valueOf(cargoItemResult.get("ci.volume_unit"))));
    }

    if (Objects.nonNull(cargoItemResult.get("ci.number_of_packages"))) {
      cargoItemWithCargoLineItems.setNumberOfPackages(
          Integer.valueOf(String.valueOf(cargoItemResult.get("ci.number_of_packages"))));
    }

    if (Objects.nonNull(cargoItemResult.get("s.carrier_booking_reference"))) {
      cargoItemWithCargoLineItems.setCarrierBookingReference(
          String.valueOf(cargoItemResult.get("s.carrier_booking_reference")));
    }

    cargoItemWithCargoLineItems.setShippingInstructionID(
        String.valueOf(cargoItemResult.get("ci.shipping_instruction_id")));
    cargoItemWithCargoLineItems.setPackageCode(
        String.valueOf(cargoItemResult.get("ci.package_code")));
    cargoItemWithCargoLineItems.setShipmentEquipmentID(
        UUID.fromString(String.valueOf(cargoItemResult.get("ci.shipment_equipment_id"))));
    cargoItemWithCargoLineItems.setCargoLineItems(cargoLineItems);

    return cargoItemWithCargoLineItems;
  }

  private Map<String, Column> queryColumnMap() {
    Map<String, Column> selectedColumns = new HashMap<>();
    selectedColumns.put("ci.id", Column.create("id", CARGO_ITEM_TABLE));
    selectedColumns.put("ci.shipment_id", Column.create("shipment_id", CARGO_ITEM_TABLE));
    selectedColumns.put(
        "ci.description_of_goods", Column.create("description_of_goods", CARGO_ITEM_TABLE));
    selectedColumns.put("ci.hs_code", Column.create("hs_code", CARGO_ITEM_TABLE));
    selectedColumns.put("ci.weight", Column.create("weight", CARGO_ITEM_TABLE));
    selectedColumns.put("ci.volume", Column.create("volume", CARGO_ITEM_TABLE));
    selectedColumns.put("ci.weight_unit", Column.create("weight_unit", CARGO_ITEM_TABLE));
    selectedColumns.put("ci.volume_unit", Column.create("volume_unit", CARGO_ITEM_TABLE));
    selectedColumns.put(
        "ci.number_of_packages", Column.create("number_of_packages", CARGO_ITEM_TABLE));
    selectedColumns.put(
        "ci.shipping_instruction_id", Column.create("shipping_instruction_id", CARGO_ITEM_TABLE));
    selectedColumns.put("ci.package_code", Column.create("package_code", CARGO_ITEM_TABLE));
    selectedColumns.put(
        "ci.shipment_equipment_id", Column.create("shipment_equipment_id", CARGO_ITEM_TABLE));
    selectedColumns.put("s.shipment_id", Column.create("id", SHIPMENT_TABLE));
    selectedColumns.put(
        "s.carrier_booking_reference", Column.create("carrier_booking_reference", SHIPMENT_TABLE));
    selectedColumns.put(
        "cli.cargo_line_item_id", Column.create("cargo_line_item_id", CARGO_LINE_ITEM_TABLE));
    selectedColumns.put("cli.cargo_item_id", Column.create("cargo_item_id", CARGO_LINE_ITEM_TABLE));
    selectedColumns.put(
        "cli.shipping_marks", Column.create("shipping_marks", CARGO_LINE_ITEM_TABLE));
    selectedColumns.put("cli.id", Column.create("id", CARGO_LINE_ITEM_TABLE));
    return selectedColumns;
  }
}
