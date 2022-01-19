package org.dcsa.core.events.model;

import lombok.Data;
import org.dcsa.core.events.model.enums.LocationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Table("shipment_location")
public class ShipmentLocation {
  @Id private UUID id;

  @Column("shipment_id")
  private UUID shipmentID;

  @Column("booking_id")
  private UUID bookingID;

  @Column("location_id")
  private String locationID;

  @Size(max = 100)
  @Column("shipping_instruction_id ")
  private String shippingInstructionID;

  @Column("shipment_location_type_code")
  private LocationType shipmentLocationTypeCode;

  @Column("displayed_name")
  private String displayedName;

  @Column("event_date_time")
  private OffsetDateTime eventDateTime;
}
