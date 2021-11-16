package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.enums.LocationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Table("shipment_location")
public abstract class AbstractShipmentLocation {
  @JsonIgnore
  @Column("id")
  private UUID id;

  @NotNull(message = "ShipmentID is required.")
  @Column("shipment_id")
  private UUID shipmentID;

  @NotNull(message = "BookingID is required.")
  @Column("booking_id")
  private UUID bookingID;

  @Column("location_id")
  private String locationID;

  @NotNull(message = "LocationType is required.")
  @Column("shipment_location_type_code")
  private LocationType shipmentLocationTypeCode;

  @Size(max = 250, message = "DisplayName has a max size of 250.")
  @Column("displayed_name")
  private String displayedName;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Column("event_date_time")
  private OffsetDateTime eventDateTime;
}
