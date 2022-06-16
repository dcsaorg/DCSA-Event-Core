package org.dcsa.core.events.model.transferobjects;

import lombok.Data;
import org.dcsa.core.events.model.enums.VolumeUnit;
import org.dcsa.core.events.model.enums.WeightUnit;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class CargoItemTO {

  private List<CargoLineItemTO> cargoLineItems;

  @NotNull
  @Size(max = 15)
  private String equipmentReference;

  @NotNull(message = "Number of packages is required.")
  private Integer numberOfPackages;

  private Float weight;

  private Float volume;

  private WeightUnit weightUnit;

  private VolumeUnit volumeUnit;

  @NotNull(message = "Package code is required.")
  @Size(max = 3)
  private String packageCode;

  private List<ReferenceTO> references;
}
