package org.dcsa.core.events.model.transferobjects;

import lombok.Data;
import org.dcsa.core.events.model.CargoItem;
import org.dcsa.core.events.model.enums.VolumeUnit;
import org.dcsa.core.events.model.enums.WeightUnit;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@Data
public class CargoItemTO {

	private String carrierBookingReference;

	private List<CargoLineItemTO> cargoLineItems;

	@NotNull(message = "Description of goods is required.")
	private String descriptionOfGoods;

	@NotNull(message = "HS code is required.")
	private String hsCode;

	@NotNull(message = "Number of packages is required.")
	private Integer numberOfPackages;

	private Float weight;

	private Float volume;

	private WeightUnit weightUnit;

	private VolumeUnit volumeUnit;

	@NotNull(message = "Package code is required.")
	@Size(max = 3)
	private String packageCode;

	private List<ReferenceTO> references;

	public CargoItem toCargoItem(@NotNull UUID shipmentEquipmentID, String shippingInstructionID) {
		CargoItem cargoItem = new CargoItem();
		cargoItem.setShipmentEquipmentID(shipmentEquipmentID);
		cargoItem.setShippingInstructionID(shippingInstructionID);
		cargoItem.setDescriptionOfGoods(this.getDescriptionOfGoods());
		cargoItem.setHsCode(this.getHsCode());
		cargoItem.setNumberOfPackages(this.getNumberOfPackages());
		cargoItem.setWeight(this.getWeight());
		cargoItem.setWeightUnit(this.getWeightUnit());
		cargoItem.setPackageCode(this.getPackageCode());
		return cargoItem;
	}

}
