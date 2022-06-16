package org.dcsa.core.events.model.base;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.enums.VolumeUnit;
import org.dcsa.core.events.model.enums.WeightUnit;
import org.dcsa.core.model.AuditBase;
import org.springframework.data.relational.core.mapping.Column;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class AbstractCargoItem extends AuditBase {

  @NotNull
  @Column("number_of_packages")
  private Integer numberOfPackages;

  @NotNull private Float weight;

  private Float volume;

  @NotNull
  @Column("weight_unit")
  private WeightUnit weightUnit;

  @Column("volume_unit")
  private VolumeUnit volumeUnit;

  @Size(max = 3)
  @NotNull
  @Column("package_code")
  private String packageCode;
}
