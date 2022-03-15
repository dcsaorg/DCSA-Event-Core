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
  //  private static final Table SHIPMENT_TABLE = Table.create("shipment");

  // We need a LinkedHashMap because the test case relies on the order of values()
  private static final Map<String, Column> QUERY_COLUMN_MAP =
      Collections.unmodifiableMap(
          new LinkedHashMap<>() {
            {
              put("ci.id", Column.create("id", CARGO_ITEM_TABLE));
              put(
                  "ci.description_of_goods",
                  Column.create("description_of_goods", CARGO_ITEM_TABLE));
              put("ci.hs_code", Column.create("hs_code", CARGO_ITEM_TABLE));
              put("ci.weight", Column.create("weight", CARGO_ITEM_TABLE));
              put("ci.volume", Column.create("volume", CARGO_ITEM_TABLE));
              put("ci.weight_unit", Column.create("weight_unit", CARGO_ITEM_TABLE));
              put("ci.volume_unit", Column.create("volume_unit", CARGO_ITEM_TABLE));
              put("ci.number_of_packages", Column.create("number_of_packages", CARGO_ITEM_TABLE));
              put(
                  "ci.shipping_instruction_id",
                  Column.create("shipping_instruction_id", CARGO_ITEM_TABLE));
              put("ci.package_code", Column.create("package_code", CARGO_ITEM_TABLE));
              put(
                  "ci.shipment_equipment_id",
                  Column.create("shipment_equipment_id", CARGO_ITEM_TABLE));
              put("se.id", Column.create("id", SHIPMENT_EQUIPMENT_TABLE));
              put("se.shipment_id", Column.create("shipment_id", SHIPMENT_EQUIPMENT_TABLE));
              put(
                  "cli.cargo_line_item_id",
                  Column.create("cargo_line_item_id", CARGO_LINE_ITEM_TABLE));
              put("cli.cargo_item_id", Column.create("cargo_item_id", CARGO_LINE_ITEM_TABLE));
              put("cli.shipping_marks", Column.create("shipping_marks", CARGO_LINE_ITEM_TABLE));
              put("cli.id", Column.create("id", CARGO_LINE_ITEM_TABLE));
            }
          });

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
            .where(
                Conditions.isEqual(
                    column("ci.shipment_equipment_id"),
                    SQL.literalOf(shipmentEquipmentID.toString())))
            .build();

    RenderContextFactory factory = new RenderContextFactory(r2dbcDialect);
    SqlRenderer sqlRenderer = SqlRenderer.create(factory.createRenderContext());

    return client
        .sql(sqlRenderer.render(selectJoin))
        .fetch()
        .all()
        .bufferUntilChanged(resultSet -> resultSet.get(column("ci.id").getName().getReference()))
        .map(this::mapResultSet);
  }

  CargoItemWithCargoLineItems mapResultSet(List<Map<String, Object>> cargoItemResultSet) {
    Map<String, Object> cargoItemResult = cargoItemResultSet.get(0);
    List<CargoLineItem> cargoLineItems =
        cargoItemResultSet.stream()
            .filter(
                cargoLineItemResultSet ->
                    Objects.nonNull(
                        cargoLineItemResultSet.get(
                            column("cli.cargo_item_id").getName().getReference())))
            .map(
                cargoLineItemResultSet -> {
                  CargoLineItem cargoLineItem = new CargoLineItem();
                  cargoLineItem.setCargoItemID(
                      (UUID)
                          cargoLineItemResultSet.get(
                              column("cli.cargo_item_id").getName().getReference()));
                  cargoLineItem.setCargoLineItemID(
                      String.valueOf(
                          cargoLineItemResultSet.get(
                              column("cli.cargo_line_item_id").getName().getReference())));
                  cargoLineItem.setShippingMarks(
                      String.valueOf(
                          cargoLineItemResultSet.get(
                              column("cli.shipping_marks").getName().getReference())));
                  return cargoLineItem;
                })
            .collect(Collectors.toList());
    CargoItemWithCargoLineItems cargoItemWithCargoLineItems = new CargoItemWithCargoLineItems();
    cargoItemWithCargoLineItems.setId(
        UUID.fromString(
            String.valueOf(cargoItemResult.get(column("ci.id").getName().getReference()))));
    cargoItemWithCargoLineItems.setDescriptionOfGoods(
        String.valueOf(
            cargoItemResult.get(column("ci.description_of_goods").getName().getReference())));
    cargoItemWithCargoLineItems.setHsCode(
        String.valueOf(cargoItemResult.get(column("ci.hs_code").getName().getReference())));
    cargoItemWithCargoLineItems.setWeight(
        (Float) cargoItemResult.get(column("ci.weight").getName().getReference()));

    if (Objects.nonNull(cargoItemResult.get(column("ci.weight_unit").getName().getReference()))) {
      cargoItemWithCargoLineItems.setWeightUnit(
          WeightUnit.valueOf(
              String.valueOf(
                  cargoItemResult.get(column("ci.weight_unit").getName().getReference()))));
    }

    cargoItemWithCargoLineItems.setVolume(
        (Float) cargoItemResult.get(column("ci.volume").getName().getReference()));

    if (Objects.nonNull(cargoItemResult.get(column("ci.volume_unit").getName().getReference()))) {
      cargoItemWithCargoLineItems.setVolumeUnit(
          VolumeUnit.valueOf(
              String.valueOf(
                  cargoItemResult.get(column("ci.volume_unit").getName().getReference()))));
    }

    if (Objects.nonNull(
        cargoItemResult.get(column("ci.number_of_packages").getName().getReference()))) {
      cargoItemWithCargoLineItems.setNumberOfPackages(
          Integer.valueOf(
              String.valueOf(
                  cargoItemResult.get(column("ci.number_of_packages").getName().getReference()))));
    }

    cargoItemWithCargoLineItems.setShippingInstructionReference(
        String.valueOf(
            cargoItemResult.get(column("ci.shipping_instruction_id").getName().getReference())));
    cargoItemWithCargoLineItems.setPackageCode(
        String.valueOf(cargoItemResult.get(column("ci.package_code").getName().getReference())));
    cargoItemWithCargoLineItems.setShipmentEquipmentID(
        UUID.fromString(
            String.valueOf(
                cargoItemResult.get(column("ci.shipment_equipment_id").getName().getReference()))));
    cargoItemWithCargoLineItems.setCargoLineItems(cargoLineItems);

    return cargoItemWithCargoLineItems;
  }

  private static Column column(String name) {
    assert QUERY_COLUMN_MAP.get(name) != null : "Bad column name: " + name;
    return QUERY_COLUMN_MAP.get(name);
  }
}
