package org.dcsa.core.events.edocumentation.model.transferobject;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
public
class ConfirmedEquipmentTO {

  @NotNull(message = "ConfirmedEquipmentSizeType is required.")
  @Size(max = 4, message = "ConfirmedEquipmentSizeType has a max size of 4.")
  private String confirmedEquipmentSizetype;

  @PositiveOrZero(message = "ConfirmedEquipmentUnits has to be a positive value.")
  private Integer confirmedEquipmentUnits;
}
