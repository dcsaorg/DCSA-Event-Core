package org.dcsa.core.events.model;

import lombok.Data;
import org.dcsa.core.events.model.enums.CutOffDateTimeCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Table("shipment_cutoff_time")
public class ShipmentCutOffTime {
  @Id
  @Column("shipment_id")
  private UUID shipmentID;

  @Column("cut_off_time_code")
  private CutOffDateTimeCode cutOffDateTimeCode;

  @Column("cut_off_time")
  private OffsetDateTime cutOffDateTime;
}
