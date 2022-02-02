package org.dcsa.core.events.model.transferobjects;

import lombok.Data;
import org.dcsa.core.events.model.enums.TemperatureUnit;

@Data
public class ActiveReeferSettingsTO {

	private Float temperatureMin;

	private Float temperatureMax;

	private TemperatureUnit temperatureUnit;

	private Float humidityMin;

	private Float humidityMax;

	private Float ventilationMin;

	private Float ventilationMax;
}
