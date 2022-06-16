package org.dcsa.core.events.model.transferobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.AbstractUtilizedTransportEquipment;
import org.springframework.data.annotation.Transient;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class UtilizedTransportEquipmentTO extends AbstractUtilizedTransportEquipment {

  // Strictly for internal use in consignmentItemServiceImpl
  // to find utilizedTransportEquipmentId from equipmentReference
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JsonIgnore
  @Transient
  private UUID id;

  @Size(max = 35)
  private String carrierBookingReference;

  @Valid
  @NotNull(message = "Equipment is required.")
  private EquipmentTO equipment;

  @Valid
  //  @NotEmpty
  private List<CargoItemTO> cargoItems;

  @Valid private ActiveReeferSettingsTO activeReeferSettings;

  @Valid private List<SealTO> seals;
}
