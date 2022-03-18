package org.dcsa.core.events.edocumentation.model.transferobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.dcsa.core.events.model.enums.CutOffDateTimeCode;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Data
public
class ShipmentCutOffTimeTO {

  @NotNull(message = "CutOffDateTimeCode is required.")
  private CutOffDateTimeCode cutOffDateTimeCode;

  @NotNull(message = "CutOffDateTime is required.")
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private OffsetDateTime cutOffDateTime;
}
