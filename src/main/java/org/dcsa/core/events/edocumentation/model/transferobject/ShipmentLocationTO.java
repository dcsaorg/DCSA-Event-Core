package org.dcsa.core.events.edocumentation.model.transferobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.dcsa.core.events.model.enums.LocationType;
import org.dcsa.core.events.model.transferobjects.LocationTO;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Data
public
class ShipmentLocationTO {

  @NotNull(message = "Location is required.")
  private LocationTO location;

  @NotNull(message = "LocationType is required.")
  private LocationType shipmentLocationTypeCode;

  @Size(max = 250, message = "DisplayName has a max size of 250.")
  private String displayedName;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private OffsetDateTime eventDateTime;
}
