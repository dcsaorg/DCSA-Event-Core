package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import javax.validation.constraints.Size;

@Data
@Table("equipment")
public class Equipment {

  public static class Constraints {
    private Constraints() {}

    public static final Integer EQUIPMENT_REFERENCE_SIZE = 15;
  }

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
}
