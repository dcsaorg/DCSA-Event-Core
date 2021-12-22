package org.dcsa.core.events.repository.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.enums.DocumentStatus;
import org.dcsa.core.events.repository.ShipmentCustomRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.data.relational.core.dialect.RenderContextFactory;
import org.springframework.data.relational.core.sql.*;
import org.springframework.data.relational.core.sql.render.SqlRenderer;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ShipmentCustomRepositoryImpl implements ShipmentCustomRepository {

  private final DatabaseClient client;
  private final R2dbcDialect r2dbcDialect;

  private static final Table SHIPMENT_TABLE = Table.create("shipment");
  private static final Table BOOKING_TABLE = Table.create("booking");

  @Override
  public Flux<ShipmentSummary> findShipmentsAndBookingsByDocumentStatus(
      DocumentStatus documentStatus, Pageable pageable) {

    //Creates the following query programatically, since order by is not supported in @Query
    //    select s.id, s.booking_id, s.carrier_id, s.carrier_booking_reference,
    // s.terms_and_conditions, s.confirmation_datetime, b.carrier_booking_request_reference
    //    from dcsa_im_v3_0.shipment s
    //    join dcsa_im_v3_0.booking b on s.booking_id = b.id
    //    where b.document_status = :documentStatus
    //    order by :sort
    //    LIMIT :pageable OFFSET :pageable

    SelectBuilder.SelectAndFrom selectBuilder = Select.builder().select(queryColumnMap().values());
    Select selectJoin =
        selectBuilder
            .from(SHIPMENT_TABLE)
            .join(BOOKING_TABLE)
            .on(queryColumnMap().get("bookingId"))
            .equals(queryColumnMap().get("idBooking"))
            .limitOffset(pageable.getPageSize(), pageable.getOffset())
            .where(getDocumentStatusCondition(documentStatus))
            .orderBy(sortToOrderBy(pageable.getSort()))
            .build();

    RenderContextFactory factory = new RenderContextFactory(r2dbcDialect);
    SqlRenderer sqlRenderer = SqlRenderer.create(factory.createRenderContext());

    return client.sql(sqlRenderer.render(selectJoin))
      .map(row -> new ShipmentSummary(
        row.get("carrier_booking_reference").toString(),
        row.get("terms_and_conditions").toString(),
        OffsetDateTime.parse(row.get("confirmation_datetime").toString()),
        row.get("carrier_booking_request_reference").toString(),
        DocumentStatus.valueOf(row.get("document_status").toString())
      ))
      .all();

  }

  private Condition getDocumentStatusCondition(DocumentStatus documentStatus) {
    Condition condition;
    if (documentStatus == null) {
      condition = Conditions.isNull(SQL.nullLiteral());
    } else {
      condition =
          Conditions.isEqual(
              Column.create("document_status", BOOKING_TABLE),
              SQL.literalOf(documentStatus.toString()));
    }
    return condition;
  }

  private Set<OrderByField> sortToOrderBy(Sort sort) {
    return sort.stream()
        .map(
            order ->
                OrderByField.from(queryColumnMap().get(order.getProperty()), order.getDirection()))
        .collect(Collectors.toSet());
  }

  private Map<String, Column> queryColumnMap() {
    Map<String, Column> selectedColumns = new HashMap<>();
    selectedColumns.put("id", Column.create("id", SHIPMENT_TABLE));
    selectedColumns.put("bookingId", Column.create("booking_id", SHIPMENT_TABLE));
    selectedColumns.put(
        "carrierBookingReference", Column.create("carrier_booking_reference", SHIPMENT_TABLE));
    selectedColumns.put(
        "termsAndConditions", Column.create("terms_and_conditions", SHIPMENT_TABLE));
    selectedColumns.put(
        "confirmationDateTime", Column.create("confirmation_datetime", SHIPMENT_TABLE));
    selectedColumns.put(
        "carrierBookingRequestReference",
        Column.create("carrier_booking_request_reference", BOOKING_TABLE));
    selectedColumns.put("documentStatus", Column.create("document_status", BOOKING_TABLE));
    selectedColumns.put("idBooking", Column.create("id", BOOKING_TABLE));

    return selectedColumns;
  }

}
