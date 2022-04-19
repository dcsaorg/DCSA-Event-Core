package org.dcsa.core.events.edocumentation.model.transferobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.dcsa.core.events.model.enums.CargoGrossVolume;
import org.dcsa.core.events.model.enums.CargoGrossWeight;
import org.springframework.data.relational.core.mapping.Column;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class CommodityTO {

  @NotBlank(message = "CommodityType is required.")
  @Size(max = 20, message = "CommodityType has a max size of 20.")
  private String commodityType;

  @Size(max = 10, message = "HSCode has a max size of 10.")
  @JsonProperty("HSCode")
  private String hsCode;

  @NotNull(message = "CargoGrossWeight is required.")
  private Double cargoGrossWeight;

  @NotNull(message = "CargoGrossWeightUnit is required.")
  private CargoGrossWeight cargoGrossWeightUnit;

  private Float cargoGrossVolume;

  private CargoGrossVolume cargoGrossVolumeUnit;

  @PositiveOrZero
  private Integer numberOfPackages;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private LocalDate exportLicenseIssueDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private LocalDate exportLicenseExpiryDate;
}
