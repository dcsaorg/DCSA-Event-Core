package org.dcsa.core.events.model;

import lombok.Data;
import org.dcsa.core.events.model.enums.WeightUnit;
import org.springframework.data.relational.core.mapping.Column;

import javax.validation.constraints.NotNull;

@Data
public abstract class AbstractUtilizedTransportEquipment {

  @NotNull(message = "Cargo gross weight is required.")
  @Column("cargo_gross_weight")
  private Float cargoGrossWeight;

  @NotNull(message = "Cargo gross weight unit is required.")
  @Column("cargo_gross_weight_unit")
  private WeightUnit cargoGrossWeightUnit;

  @NotNull(message = "Is shipper owned is required.")
  @Column("is_shipper_owned")
  private Boolean isShipperOwned;
}
