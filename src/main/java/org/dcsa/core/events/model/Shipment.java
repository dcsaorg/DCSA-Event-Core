package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Table("shipment")
public class Shipment {

  @JsonIgnore
  @Column("booking_id")
  private UUID bookingID;

  @JsonIgnore
  @Column("carrier_id")
  private UUID carrierID;

  @Size(max = 35)
  @Column("carrier_booking_reference")
  private String carrierBookingReferenceID;

  @Column("terms_and_conditions")
  private String termsAndConditions;

  @Column("confirmation_datetime")
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private OffsetDateTime confirmationDateTime;

  @JsonIgnore
  @Column("place_of_issue")
  @Size(max = 100)
  private String placeOfIssueID;
}
