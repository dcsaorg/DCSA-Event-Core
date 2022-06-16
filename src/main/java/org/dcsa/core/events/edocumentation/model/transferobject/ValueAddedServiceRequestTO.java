package org.dcsa.core.events.edocumentation.model.transferobject;

import lombok.Data;
import org.dcsa.core.events.model.enums.ValueAddedServiceCode;

import javax.validation.constraints.NotNull;

@Data
public class ValueAddedServiceRequestTO {
  @NotNull(message = "ValueAddedServiceCode is required.")
  private ValueAddedServiceCode valueAddedServiceCode;
}
