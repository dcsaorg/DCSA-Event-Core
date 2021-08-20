package org.dcsa.core.events.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.enums.CargoMovementType;
import org.dcsa.core.events.model.enums.ReceiptDeliveryType;
import org.dcsa.core.events.model.enums.WeightUnit;
import org.dcsa.core.model.AuditBase;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Table("booking")
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class Booking extends AuditBase {

  @Id
  @Column("carrier_booking_reference")
  @Size(max = 35)
  private String id;

  @Column("receipt_delivery_type_at_origin")
  private ReceiptDeliveryType receiptDeliveryTypeAtOrigin;

  @Column("receipt_delivery_type_at_destination")
  private ReceiptDeliveryType receiptDeliveryTypeAtDestination;

  @Column("cargo_movement_type_at_origin")
  private CargoMovementType cargo_movement_type_at_origin;

  @Column("cargo_movement_type_at_destination")
  private CargoMovementType cargo_movement_type_at_destination;

  @Column("booking_request_datetime")
  private OffsetDateTime bookingDateTime;

  @Column("service_contract")
  @Size(max = 30)
  private String serviceContract;

  @Column("cargo_gross_weight")
  private Float cargoGrossWeight;

  @Column("cargo_gross_weight_unit")
  @Size(max = 3)
  private WeightUnit cargoGrossWeightUnit;

  @Column("commodity_type")
  @Size(max = 20)
  private String commodityType;
}
