package org.dcsa.core.events.model;

import lombok.Data;
import org.dcsa.core.events.model.enums.CargoGrossWeight;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Table("requested_equipment_equipment")
public class RequestedEquipmentEquipment {

  @NotNull
  @Column("requested_equipment_id")
  private UUID requestedEquipmentId;

  @NotNull
  @Size(max = 15)
  @Column("equipment_reference")
  private String equipmentReference;
}
