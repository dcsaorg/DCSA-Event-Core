package org.dcsa.core.events.model.transferobjects;

import lombok.Data;
import org.dcsa.core.events.model.Seal;
import org.dcsa.core.events.model.enums.SealSourceCode;
import org.dcsa.core.events.model.enums.SealTypeCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
public class SealTO {

	@Size(max = 15)
	@NotNull(message = "Seal number is required.")
	private String sealNumber;

	private SealSourceCode sealSource;

	@NotNull(message = "Seal type is required.")
	private SealTypeCode sealType;

	public Seal toSeal(UUID shipmentEquipmentID) {
		Seal seal = new Seal();
		seal.setShipmentEquipmentID(shipmentEquipmentID);
		seal.setSealNumber(this.getSealNumber());
		seal.setSealSource(this.getSealSource().name());
		seal.setSealType(this.getSealType().name());
		return seal;
	}
}
