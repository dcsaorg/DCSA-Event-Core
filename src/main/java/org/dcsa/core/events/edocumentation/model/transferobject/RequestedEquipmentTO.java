package org.dcsa.core.events.edocumentation.model.transferobject;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class RequestedEquipmentTO {

  @NotBlank(message = "RequestedEquipmentSizeType is required.")
  @Size(max = 4, message = "RequestedEquipmentSizeType has a max size of 4.")
  private String requestedEquipmentSizeType;

  @PositiveOrZero(message = "RequestedEquipmentUnits has to be a positive value.")
  private Integer requestedEquipmentUnits;

  private List<String> equipmentReferences;

  @JsonProperty("isShipperOwned")
  private boolean isShipperOwned;
}
