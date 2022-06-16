package org.dcsa.core.events.edocumentation.model.transferobject;

import lombok.Data;
import org.dcsa.core.events.model.enums.ShipmentEventTypeCode;
import org.dcsa.core.validator.EnumSubset;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

import static org.dcsa.core.events.model.enums.ShipmentEventTypeCode.BOOKING_DOCUMENT_STATUSES;

@Data
public class BookingResponseTO {
  @NotNull private String carrierBookingRequestReference;

  @EnumSubset(anyOf = BOOKING_DOCUMENT_STATUSES)
  @NotNull private ShipmentEventTypeCode documentStatus;

  @NotNull private OffsetDateTime bookingRequestCreatedDateTime;

  @NotNull private OffsetDateTime bookingRequestUpdatedDateTime;
}
