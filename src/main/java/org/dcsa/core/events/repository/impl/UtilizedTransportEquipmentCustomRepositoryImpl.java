package org.dcsa.core.events.repository.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.enums.WeightUnit;
import org.dcsa.core.events.repository.UtilizedTransportEquipmentCustomRepository;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.data.relational.core.dialect.RenderContextFactory;
import org.springframework.data.relational.core.sql.*;
import org.springframework.data.relational.core.sql.render.SqlRenderer;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class UtilizedTransportEquipmentCustomRepositoryImpl implements UtilizedTransportEquipmentCustomRepository {

  private final R2dbcDialect r2dbcDialect;
  private final DatabaseClient client;

  private static final Table SHIPMENT_EQUIPMENT_TABLE = Table.create("shipment_equipment");
  private static final Table EQUIPMENT_TABLE = Table.create("equipment");
  private static final Table SHIPMENT_TABLE = Table.create("shipment");

  @Override
  public Flux<UtilizedTransportEquipmentDetails> findUtilizedTransportEquipmentDetailsByShipmentID(UUID shipmentID) {
    // creates the following query programmatically
    // select se.id,se.shipment_id,se.equipment_reference,se.cargo_gross_weight,
    // se.cargo_gross_weight_unit,se.is_shipper_owned,e.iso_equipment_code,e.tare_weight,
    // e.weight_unit
    // from dcsa_im_v3_0.shipment_equipment se
    // join dcsa_im_v3_0.equipmente on e.equipment_reference=se.equipment_reference
    // where se.equipment_reference=:equipment_reference

    Select selectJoin =
        Select.builder()
            .select(queryColumnMap().values())
            .from(SHIPMENT_EQUIPMENT_TABLE)
            .join(EQUIPMENT_TABLE)
            .on(queryColumnMap().get("seEquipmentReference"))
            .equals(queryColumnMap().get("eEquipmentReference"))
            .join(SHIPMENT_TABLE)
            .on(queryColumnMap().get("shipmentId"))
            .equals(queryColumnMap().get("sShipmentId"))
            .where(
                Conditions.isEqual(
                    queryColumnMap().get("shipmentId"), SQL.literalOf(shipmentID.toString())))
            .build();

    RenderContextFactory factory = new RenderContextFactory(r2dbcDialect);
    SqlRenderer sqlRenderer = SqlRenderer.create(factory.createRenderContext());
    return client
        .sql(sqlRenderer.render(selectJoin))
        .map(
            row ->
                new UtilizedTransportEquipmentDetails(
                    row.get(
                        queryColumnMap().get("carrierBookingReference").getReferenceName().getReference(),
                        String.class),
                    row.get(
                        queryColumnMap().get("seEquipmentReference").getReferenceName().getReference(),
                        String.class),
                    row.get(
                        queryColumnMap().get("cargoGrossWeight").getReferenceName().getReference(),
                        Float.class),
                    WeightUnit.valueOf(
                        row.get(
                            queryColumnMap().get("cargoGrossWeightUnit").getReferenceName().getReference(),
                            String.class)),
                    row.get(
                        queryColumnMap().get("isoEquipmentCode").getReferenceName().getReference(),
                        String.class),
                    row.get(
                        queryColumnMap().get("tareWeight").getReferenceName().getReference(), Float.class),
                    row.get(
                        queryColumnMap().get("weightUnit").getReferenceName().getReference(), String.class),
                    row.get(
                        queryColumnMap().get("isShipperOwned").getReferenceName().getReference(),
                        Boolean.class),
                    row.get(queryColumnMap().get("id").getReferenceName().getReference(), UUID.class)))
        .all();
  }

  private Map<String, Column> queryColumnMap() {
    Map<String, Column> selectedColumns = new HashMap<>();
    selectedColumns.put("id", Column.create("id", SHIPMENT_EQUIPMENT_TABLE));
    selectedColumns.put(
        "seEquipmentReference", Column.create("equipment_reference", SHIPMENT_EQUIPMENT_TABLE));
    selectedColumns.put(
        "eEquipmentReference", Column.create("equipment_reference", EQUIPMENT_TABLE));
    selectedColumns.put("shipmentId", Column.create("shipment_id", SHIPMENT_EQUIPMENT_TABLE).as("shipmentId"));
    selectedColumns.put("sShipmentId", Column.create("id", SHIPMENT_TABLE).as("sShipmentId"));
    selectedColumns.put(
        "carrierBookingReference", Column.create("carrier_booking_reference", SHIPMENT_TABLE));
    selectedColumns.put(
        "cargoGrossWeight", Column.create("cargo_gross_weight", SHIPMENT_EQUIPMENT_TABLE));
    selectedColumns.put(
        "cargoGrossWeightUnit", Column.create("cargo_gross_weight_unit", SHIPMENT_EQUIPMENT_TABLE));
    selectedColumns.put(
        "isShipperOwned", Column.create("is_shipper_owned", SHIPMENT_EQUIPMENT_TABLE));
    selectedColumns.put("isoEquipmentCode", Column.create("iso_equipment_code", EQUIPMENT_TABLE));
    selectedColumns.put("tareWeight", Column.create("tare_weight", EQUIPMENT_TABLE));
    selectedColumns.put("weightUnit", Column.create("weight_unit", EQUIPMENT_TABLE));
    return selectedColumns;
  }
}
