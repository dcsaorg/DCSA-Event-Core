package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.transferobjects.*;
import org.dcsa.core.model.GetId;
import org.dcsa.core.util.MappingUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@Table("shipment_equipment")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ShipmentEquipment extends AbstractShipmentEquipment implements GetId<UUID> {

  @Id
  @Column("id")
  private UUID id;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Column("shipment_id")
  private UUID shipmentID;

  @Column("equipment_reference")
  @Size(max = 15)
  private String equipmentReference;

	public ShipmentEquipmentTO toShipmentEquipmentTO(EquipmentTO equipment, List<CargoItemTO> cargoItems, List<SealTO> seals, ActiveReeferSettingsTO activeReeferSettings) {
    ShipmentEquipmentTO shipmentEquipmentTO = MappingUtils.instanceFrom(this, ShipmentEquipmentTO::new, AbstractShipmentEquipment.class);
    //ToDo add null checks
    shipmentEquipmentTO.setEquipment(equipment);
    shipmentEquipmentTO.setCargoItems(cargoItems);
    shipmentEquipmentTO.setSeals(seals);
    shipmentEquipmentTO.setActiveReeferSettings(activeReeferSettings);
    return shipmentEquipmentTO;
	}

  public ShipmentEquipmentTO toShipmentEquipmentTO() {
    return MappingUtils.instanceFrom(this, ShipmentEquipmentTO::new, AbstractShipmentEquipment.class);
  }
}
