package org.dcsa.core.events.model.transferobjects;

import lombok.Data;
import org.dcsa.core.events.model.ActiveReeferSettings;
import org.dcsa.core.events.model.enums.TemperatureUnit;

import javax.validation.constraints.Size;
import java.util.UUID;

@Data
public class ActiveReeferSettingsTO {

	private Float temperatureMin;

	private Float temperatureMax;

	@Size(max = 3)
	private TemperatureUnit temperatureUnit;

	private Float humidityMin;

	private Float humidityMax;

	private Float ventilationMin;

	private Float ventilationMax;

	public ActiveReeferSettings toActiveReeferSettings(UUID shipmentEquipmentID) {
		ActiveReeferSettings activeReeferSettings = new ActiveReeferSettings();
		activeReeferSettings.setShipmentEquipmentID(shipmentEquipmentID);
		activeReeferSettings.setTemperatureMin(this.getTemperatureMin());
		activeReeferSettings.setTemperatureMax(this.getTemperatureMax());
		activeReeferSettings.setTemperatureUnit(this.getTemperatureUnit());
		activeReeferSettings.setHumidityMin(this.getHumidityMin());
		activeReeferSettings.setHumidityMax(this.getHumidityMax());
		activeReeferSettings.setVentilationMin(this.getVentilationMin());
		activeReeferSettings.setVentilationMax(this.getVentilationMax());
		return activeReeferSettings;
	}
}
