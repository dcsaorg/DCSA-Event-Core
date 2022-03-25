package org.dcsa.core.events.edocumentation.model.transferobject;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import org.dcsa.core.events.model.transferobjects.CargoItemTO;
import org.dcsa.core.events.model.transferobjects.ReferenceTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.List;

@Value
@Builder
public class ConsignmentItemTO {
  public enum WeightUnit {
    KGM, LBR
  }
  public enum VolumeUnit {
    CBM, CFT
  }

  // Used to look up shipmentId
  @Size(max = 35)
  private String carrierBookingReference;

  @NotNull
  private Double weight;

  @NotNull
  private WeightUnit weightUnit;

  private Double volume;

  private VolumeUnit volumeUnit;

  @NotBlank
  private String descriptionOfGoods;

  @Size(max = 10)
  @JsonProperty("HSCode")
  private String hsCode;

  private List<CargoItemTO> cargoItems = Collections.emptyList();
  private List<ReferenceTO> references = Collections.emptyList();
}
