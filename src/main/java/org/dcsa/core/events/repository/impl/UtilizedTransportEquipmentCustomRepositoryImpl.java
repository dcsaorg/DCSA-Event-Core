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

  private static final Table UTILIZED_TRANSPORT_EQUIPMENT_TABLE = Table.create("utilized_transport_equipment");
  private static final Table EQUIPMENT_TABLE = Table.create("equipment");
  private static final Table SHIPMENT_TABLE = Table.create("shipment");
  private static final Table CONSIGNMENT_ITEM_TABLE = Table.create("consignment_item");
  private static final Table CARGO_ITEM_TABLE = Table.create("cargo_item");


  @Override
  public Flux<UtilizedTransportEquipmentDetails> findUtilizedTransportEquipmentDetailsByShipmentID(UUID shipmentID) {
    // creates the following query programmatically
    // select ute.id,se.shipment_id,ute.equipment_reference,ute.cargo_gross_weight,
    // ute.cargo_gross_weight_unit,ute.is_shipper_owned,e.iso_equipment_code,e.tare_weight,
    // e.weight_unit
    // from dcsa_im_v3_0.utilized_transport_equipment ute
    // join dcsa_im_v3_0.equipment e on e.equipment_reference=ute.equipment_reference
    // where ute.equipment_reference=:equipment_reference

    // Going through cargo and consignment items:
    //  SELECT ute.* FROM dcsa_im_v3_0.utilized_transport_equipment ute
    //  	JOIN dcsa_im_v3_0.cargo_item ca ON ute.id = ca.utilized_transport_equipment_id
    //	  JOIN dcsa_im_v3_0.consignment_item co ON co.id = ca.consignment_item_id
    //	  JOIN dcsa_im_v3_0.shipment s ON s.id = co.shipment_id
    //	  WHERE s.id = '2504ea06-7644-4d82-8f96-df9934d0fb3c'

    Select selectJoin =
        Select.builder()
            .select(queryColumnMap().values())
            .from(UTILIZED_TRANSPORT_EQUIPMENT_TABLE)

            .join(EQUIPMENT_TABLE)
            .on(queryColumnMap().get("uteEquipmentReference"))
            .equals(queryColumnMap().get("eEquipmentReference"))

            // JOIN dcsa_im_v3_0.cargo_item ca ON ute.id = ca.utilized_transport_equipment_id
            .join(CARGO_ITEM_TABLE)
            .on(Column.create("id", UTILIZED_TRANSPORT_EQUIPMENT_TABLE))
            .equals(Column.create("utilized_transport_equipment_id", CARGO_ITEM_TABLE))

            // JOIN dcsa_im_v3_0.consignment_item co ON co.id = ca.consignment_item_id
            .join(CONSIGNMENT_ITEM_TABLE)
            .on(Column.create("id", CONSIGNMENT_ITEM_TABLE))
            .equals(Column.create("consignment_item_id", CARGO_ITEM_TABLE))

            // JOIN dcsa_im_v3_0.shipment s ON s.id = co.shipment_id
            .join(SHIPMENT_TABLE)
            .on(Column.create("id", SHIPMENT_TABLE))
            .equals(Column.create("shipment_id", CONSIGNMENT_ITEM_TABLE))

            .where(Conditions.isEqual(queryColumnMap().get("shipmentId"), SQL.literalOf(shipmentID.toString())))
            .build();

    RenderContextFactory factory = new RenderContextFactory(r2dbcDialect);
    SqlRenderer sqlRenderer = SqlRenderer.create(factory.createRenderContext());
    return client
        .sql(sqlRenderer.render(selectJoin))
        .map(
            row ->
                new UtilizedTransportEquipmentDetails(
                    row.get(queryColumnMap().get("carrierBookingReference").getReferenceName().getReference(), String.class),
                    row.get(queryColumnMap().get("uteEquipmentReference").getReferenceName().getReference(), String.class),
                    row.get(queryColumnMap().get("cargoGrossWeight").getReferenceName().getReference(), Float.class),
                    WeightUnit.valueOf(row.get(queryColumnMap().get("cargoGrossWeightUnit").getReferenceName().getReference(), String.class)),
                    row.get(queryColumnMap().get("isoEquipmentCode").getReferenceName().getReference(), String.class),
                    row.get(queryColumnMap().get("tareWeight").getReferenceName().getReference(), Float.class),
                    row.get(queryColumnMap().get("weightUnit").getReferenceName().getReference(), String.class),
                    row.get(queryColumnMap().get("isShipperOwned").getReferenceName().getReference(), Boolean.class),
                    row.get(queryColumnMap().get("id").getReferenceName().getReference(), UUID.class)
                )
        )
        .all();
  }

  private Map<String, Column> queryColumnMap() {
    Map<String, Column> selectedColumns = new HashMap<>();
    selectedColumns.put("id", Column.create("id", UTILIZED_TRANSPORT_EQUIPMENT_TABLE));
    selectedColumns.put("uteEquipmentReference", Column.create("equipment_reference", UTILIZED_TRANSPORT_EQUIPMENT_TABLE));
    selectedColumns.put("eEquipmentReference", Column.create("equipment_reference", EQUIPMENT_TABLE));
    selectedColumns.put("shipmentId", Column.create("id", SHIPMENT_TABLE).as("shipmentId"));
    selectedColumns.put("carrierBookingReference", Column.create("carrier_booking_reference", SHIPMENT_TABLE));
    selectedColumns.put("cargoGrossWeight", Column.create("cargo_gross_weight", UTILIZED_TRANSPORT_EQUIPMENT_TABLE));
    selectedColumns.put("cargoGrossWeightUnit", Column.create("cargo_gross_weight_unit", UTILIZED_TRANSPORT_EQUIPMENT_TABLE));
    selectedColumns.put("isShipperOwned", Column.create("is_shipper_owned", UTILIZED_TRANSPORT_EQUIPMENT_TABLE));
    selectedColumns.put("isoEquipmentCode", Column.create("iso_equipment_code", EQUIPMENT_TABLE));
    selectedColumns.put("tareWeight", Column.create("tare_weight", EQUIPMENT_TABLE));
    selectedColumns.put("weightUnit", Column.create("weight_unit", EQUIPMENT_TABLE));
    return selectedColumns;
  }
}
