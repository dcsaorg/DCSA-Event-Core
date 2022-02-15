package org.dcsa.core.events.edocumentation.model.transferobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class ShipmentTO {

  @NotNull(message = "CarrierBookingReference is required.")
  @Size(max = 35, message = "CarrierBookingReference has a max size of 35.")
  private String carrierBookingReference;

  private String termsAndConditions;

  @NotNull(message = "ShipmentCreatedDateTime is required.")
  private OffsetDateTime shipmentCreatedDateTime;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  protected OffsetDateTime shipmentUpdatedDateTime;

  private BookingTO booking;

  private List<TransportTO> transports;

  private List<ShipmentCutOffTimeTO> shipmentCutOffTimes;

  private List<ShipmentLocationTO> shipmentLocations;

  private List<ConfirmedEquipmentTO> confirmedEquipments;

  private List<ChargeTO> charges;

  private List<CarrierClauseTO> carrierClauses;
}
