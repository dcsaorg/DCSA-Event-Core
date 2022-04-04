package org.dcsa.core.events.edocumentation.model.transferobject;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.dcsa.core.events.model.enums.VolumeUnit;
import org.dcsa.core.events.model.enums.WeightUnit;
import org.dcsa.core.events.model.transferobjects.CargoItemTO;
import org.dcsa.core.events.model.transferobjects.ReferenceTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Value
@Builder(toBuilder = true)
public class ConsignmentItemTO {
  @With
  @Size(max = 35)
  String carrierBookingReference;

  @NotNull
  Double weight;

  @NotNull
  WeightUnit weightUnit;

  Double volume;

  VolumeUnit volumeUnit;

  @NotBlank
  String descriptionOfGoods;

  @Size(max = 10)
  @JsonProperty("HSCode")
  String hsCode;

  @NotEmpty
  List<CargoItemTO> cargoItems;

  List<ReferenceTO> references;
}
