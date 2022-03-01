package org.dcsa.core.events.model.transferobjects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.AbstractShipmentEquipment;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class ShipmentEquipmentTO extends AbstractShipmentEquipment {

	@Valid
	@NotNull(message = "Equipment is required.")
	EquipmentTO equipment;

	@Valid
	@NotEmpty
	private List<CargoItemTO> cargoItems;

	@Valid
	private ActiveReeferSettingsTO activeReeferSettings;

	@Valid
	private List<SealTO> seals;
}
