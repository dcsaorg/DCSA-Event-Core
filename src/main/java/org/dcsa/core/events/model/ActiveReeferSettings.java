package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.enums.TemperatureUnit;
import org.dcsa.core.events.model.transferobjects.ActiveReeferSettingsTO;
import org.dcsa.core.model.AuditBase;
import org.dcsa.core.model.GetId;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import javax.validation.constraints.Size;
import java.util.UUID;

@NoArgsConstructor
@Data
public class ActiveReeferSettings extends AuditBase implements GetId<UUID> {

    @Id
    /* We do not show this in JSON as it is an internal detail */
    @JsonIgnore
    @Column("shipment_equipment_id")
    private UUID shipmentEquipmentID;

    @Column("temperature_min")
    private Float temperatureMin;

    @Column("temperature_max")
    private Float temperatureMax;

    @Column("temperature_unit")
    @Size(max = 3)
    private TemperatureUnit temperatureUnit;
    
    @Column("humidity_min")
    private Float humidityMin;

    @Column("humidity_max")
    private Float humidityMax;

    @Column("ventilation_min")
    private Float ventilationMin;

    @Column("ventilation_max")
    private Float ventilationMax;

    @Override
    @JsonIgnore
    public UUID getId() {
        return getShipmentEquipmentID();
    }

    public ActiveReeferSettingsTO toActiveReeferSettingsTO() {
        ActiveReeferSettingsTO activeReeferSettingsTO = new ActiveReeferSettingsTO();
        activeReeferSettingsTO.setTemperatureMin(this.getTemperatureMin());
        activeReeferSettingsTO.setTemperatureMax(this.getTemperatureMax());
        activeReeferSettingsTO.setTemperatureUnit(this.getTemperatureUnit());
        activeReeferSettingsTO.setHumidityMin(this.getHumidityMin());
        activeReeferSettingsTO.setHumidityMax(this.getHumidityMax());
        activeReeferSettingsTO.setVentilationMin(this.getVentilationMin());
        activeReeferSettingsTO.setVentilationMax(this.getVentilationMax());
        return activeReeferSettingsTO;
    }
}
