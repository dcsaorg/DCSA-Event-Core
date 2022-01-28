package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.dcsa.core.events.model.enums.WeightUnit;
import org.dcsa.core.events.model.transferobjects.EquipmentTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Size;

@Data
@Table("equipment")
public class Equipment {

  @Id
  @Column("equipment_reference")
  @Size(max = 15)
  private String equipmentReference;

  @JsonProperty("ISOEquipmentCode")
  @Column("iso_equipment_code")
  @Size(max = 4)
  private String isoEquipmentCode;

  @Column("tare_weight")
  private Float tareWeight;

  @Column("weight_unit")
  @Size(max = 3)
  private String weightUnit;

  public EquipmentTO toEquipmentTO() {
    EquipmentTO equipmentTO = new EquipmentTO();
    equipmentTO.setEquipmentReference(this.getEquipmentReference());
    equipmentTO.setIsoEquipmentCode(this.getIsoEquipmentCode());
    equipmentTO.setTareWeight(this.getTareWeight());
    equipmentTO.setWeightUnit(WeightUnit.valueOf(this.getWeightUnit()));
    return equipmentTO;
  }
}
