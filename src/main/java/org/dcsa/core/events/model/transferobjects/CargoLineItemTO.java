package org.dcsa.core.events.model.transferobjects;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CargoLineItemTO {

	@NotNull(message = "Cargo line item id is required.")
	private String cargoLineItemID;

	@NotNull(message = "Shipping marks is required.")
	private String shippingMarks;
}
