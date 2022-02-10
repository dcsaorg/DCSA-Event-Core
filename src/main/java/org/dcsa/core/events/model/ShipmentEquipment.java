package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.model.GetId;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Table("shipment_equipment")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ShipmentEquipment extends AbstractShipmentEquipment implements GetId<UUID> {

  @Id
  @JsonIgnore
  @Column("id")
  private UUID id;

  @JsonIgnore
  @NotNull
  @Column("shipment_id")
  private UUID shipmentID;

  @NotNull
  @Size(max = 15)
  @Column("equipment_reference")
  private String equipmentReference;
}
