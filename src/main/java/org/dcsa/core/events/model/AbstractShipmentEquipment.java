package org.dcsa.core.events.model;

import lombok.Data;
import org.dcsa.core.events.model.enums.WeightUnit;
import org.springframework.data.relational.core.mapping.Column;

import javax.validation.constraints.NotNull;

@Data
public abstract class AbstractShipmentEquipment {

  @NotNull(message = "Cargo gross weight is required.")
  @Column("cargo_gross_weight")
  private Float cargoGrossWeight;

  @NotNull(message = "Cargo gross weight unit is required.")
  @Column("cargo_gross_weight_unit")
  private WeightUnit cargoGrossWeightUnit;

  //ToDo add isShipperOwned here after decision in conversation with Henrik and Nicolas
}
