package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.enums.TemperatureUnit;
import org.dcsa.core.model.AuditBase;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@NoArgsConstructor
@Data
@Table("active_reefer_settings")
@EqualsAndHashCode(callSuper = true)
public class ActiveReeferSettings extends AuditBase implements Persistable<UUID> {

    @Id
    /* We do not show this in JSON as it is an internal detail */
    @Column("shipment_equipment_id")
    private UUID shipmentEquipmentID;

    @Transient
    @JsonIgnore
    private boolean isNewRecord;

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
    public UUID getId() {
        return this.shipmentEquipmentID;
    }

    @Override
    public boolean isNew() {
        return this.isNewRecord || this.getId() == null;
    }
}
