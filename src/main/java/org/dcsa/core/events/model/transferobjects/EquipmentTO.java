package org.dcsa.core.events.model.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.dcsa.core.events.model.enums.WeightUnit;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class EquipmentTO {

	@NotNull(message = "EquipmentReference is required.")
	@Size(max = 15)
	private String equipmentReference;

	@JsonProperty("ISOEquipmentCode")
	@Size(max = 4)
	private String isoEquipmentCode;

	private Float tareWeight;

	private WeightUnit weightUnit;
}
