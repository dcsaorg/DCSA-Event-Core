package org.dcsa.core.events.model.transferobjects;

import lombok.Data;
import org.dcsa.core.events.model.CargoLineItem;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class CargoLineItemTO {

	@NotNull(message = "Cargo line item id is required.")
	private String cargoLineItemID;

	@NotNull(message = "Shipping marks is required.")
	private String shippingMarks;

	public CargoLineItem toCargoLineItem(UUID cargoItemID) {
		CargoLineItem cargoLineItem = new CargoLineItem();
		cargoLineItem.setCargoItemID(cargoItemID);
		cargoLineItem.setShippingMarks(this.getShippingMarks());
		return cargoLineItem;
	}
}
