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
  private static final Table SHIPMENT_EQUIPMENT_TABLE = Table.create("shipment_equipment");
  private static final Table SHIPMENT_TABLE = Table.create("shipment");

  // We need a LinkedHashMap because the test case relies on the order of values()
  private static final Map<String, Column> QUERY_COLUMN_MAP = Collections.unmodifiableMap(new LinkedHashMap<>() {{
    put("ci.id", Column.create("id", CARGO_ITEM_TABLE));
    put(
            "ci.description_of_goods", Column.create("description_of_goods", CARGO_ITEM_TABLE));
    put("ci.hs_code", Column.create("hs_code", CARGO_ITEM_TABLE));
    put("ci.weight", Column.create("weight", CARGO_ITEM_TABLE));
    put("ci.volume", Column.create("volume", CARGO_ITEM_TABLE));
    put("ci.weight_unit", Column.create("weight_unit", CARGO_ITEM_TABLE));
    put("ci.volume_unit", Column.create("volume_unit", CARGO_ITEM_TABLE));
    put(
            "ci.number_of_packages", Column.create("number_of_packages", CARGO_ITEM_TABLE));
    put(
            "ci.shipping_instruction_id", Column.create("shipping_instruction_id", CARGO_ITEM_TABLE));
    put("ci.package_code", Column.create("package_code", CARGO_ITEM_TABLE));
    put(
            "ci.shipment_equipment_id", Column.create("shipment_equipment_id", CARGO_ITEM_TABLE));
    put("se.id", Column.create("id", SHIPMENT_EQUIPMENT_TABLE));
    put("se.shipment_id", Column.create("shipment_id", SHIPMENT_EQUIPMENT_TABLE));
    put("s.shipment_id", Column.create("id", SHIPMENT_TABLE));
    put(
            "s.carrier_booking_reference", Column.create("carrier_booking_reference", SHIPMENT_TABLE));
    put(
            "cli.cargo_line_item_id", Column.create("cargo_line_item_id", CARGO_LINE_ITEM_TABLE));
    put("cli.cargo_item_id", Column.create("cargo_item_id", CARGO_LINE_ITEM_TABLE));
    put(
            "cli.shipping_marks", Column.create("shipping_marks", CARGO_LINE_ITEM_TABLE));
    put("cli.id", Column.create("id", CARGO_LINE_ITEM_TABLE));
  }});

  @Override
  public Flux<CargoItemWithCargoLineItems> findAllCargoItemsAndCargoLineItemsByShipmentEquipmentID(
      UUID shipmentEquipmentID) {

    //	Programmatically creates the below query:
    //		SELECT ci.id, ci.description_of_goods, ci.hs_code, ci.weight, ci.volume,
    // ci.weight_unit, ci.volume_unit, ci.number_of_packages, ci.shipping_instruction_id,
    //		ci.package_code, ci.shipment_equipment_id, cli.cargo_line_item_id, cli.shipping_marks,
    // cli.id
    //		FROM dcsa_im_v3_0.cargo_item ci
    //		join dcsa_im_v3_0.cargo_line_item cli on cli.cargo_item_id = ci.id
    //		where ci.shipment_equipment_id = :shipmentEquipmentID;

    Objects.requireNonNull(shipmentEquipmentID, "ShipmentEquiment must not be null");

    Select selectJoin =
        Select.builder()
            .select(QUERY_COLUMN_MAP.values())
            .from(CARGO_ITEM_TABLE)
            .join(CARGO_LINE_ITEM_TABLE)
            .on(column("cli.cargo_item_id"))
            .equals(column("ci.id"))
            .join(SHIPMENT_EQUIPMENT_TABLE)
            .on(column("ci.shipment_equipment_id"))
            .equals(column("se.id"))
            .join(SHIPMENT_TABLE)
            .on(column("se.shipment_id"))
            .equals(column("s.shipment_id"))
            .where(
                Conditions.isEqual(
                    column("ci.shipment_equipment_id"),
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

    cargoItemWithCargoLineItems.setShippingInstructionReference(
        String.valueOf(cargoItemResult.get("ci.shipping_instruction_id")));
    cargoItemWithCargoLineItems.setPackageCode(
        String.valueOf(cargoItemResult.get("ci.package_code")));
    cargoItemWithCargoLineItems.setShipmentEquipmentID(
        UUID.fromString(String.valueOf(cargoItemResult.get("ci.shipment_equipment_id"))));
    cargoItemWithCargoLineItems.setCargoLineItems(cargoLineItems);

    return cargoItemWithCargoLineItems;
  }

  private static Column column(String name) {
    assert QUERY_COLUMN_MAP.get(name) != null : "Bad column name: " + name;
    return QUERY_COLUMN_MAP.get(name);
  }
}
