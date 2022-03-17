package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Table("shipment")
public class Shipment {

  @Id
  @JsonIgnore
  @Column("id")
  private UUID shipmentID;

  @Column("booking_id")
  @JsonIgnore
  @NotNull(message = "BookingID is required.")
  private UUID bookingID;

  @Column("carrier_id")
  @JsonIgnore
  @NotNull(message = "CarrierID is required.")
  private UUID carrierID;

  @NotNull(message = "CarrierBookingReference is required.")
  @Size(max = 35, message = "CarrierBookingReference has a max size of 35.")
  @Column("carrier_booking_reference")
  private String carrierBookingReference;

  @Column("terms_and_conditions")
  private String termsAndConditions;

  @Column("confirmation_datetime")
  @NotNull(message = "ConfirmedDateTime is required.")
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @JsonProperty("shipmentCreatedDateTime")
  private OffsetDateTime confirmationDateTime;

  // updatedDateTime is metadata to avoid having to query shipment_event for updated dateTime.
  // This is not part of the official IM model. They are added in the sql only.

  @Column("updated_date_time")
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @JsonProperty("shipmentUpdatedDateTime")
  protected OffsetDateTime updatedDateTime;
}
