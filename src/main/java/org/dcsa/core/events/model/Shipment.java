package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
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
  private OffsetDateTime confirmationDateTime;

  @Column("place_of_issue")
  @Size(max = 100)
  private String placeOfIssueID;
}
