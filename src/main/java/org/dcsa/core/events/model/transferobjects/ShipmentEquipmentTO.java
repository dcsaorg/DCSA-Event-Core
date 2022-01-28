package org.dcsa.core.events.model.transferobjects;

import lombok.Data;
import org.dcsa.core.events.model.AbstractShipmentEquipment;
import org.dcsa.core.events.model.ShipmentEquipment;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
public class ShipmentEquipmentTO extends AbstractShipmentEquipment {

	@Valid
	@NotNull(message = "Equipment is required.")
	EquipmentTO equipment;

	@Valid
	private List<CargoItemTO> cargoItems;

	@Valid
	private ActiveReeferSettingsTO activeReeferSettings;

	@Valid
	private List<SealTO> seals;

	public ShipmentEquipment toShipmentEquipment(UUID shipmentID) {
		ShipmentEquipment shipmentEquipment = new ShipmentEquipment();
		shipmentEquipment.setShipmentID(shipmentID);
		shipmentEquipment.setEquipmentReference(this.getEquipment().getEquipmentReference());
		shipmentEquipment.setCargoGrossWeight(this.getCargoGrossWeight());
		shipmentEquipment.setCargoGrossWeightUnit(this.getCargoGrossWeightUnit());
		return shipmentEquipment;
	}
}
