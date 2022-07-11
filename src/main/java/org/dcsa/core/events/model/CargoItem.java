package org.dcsa.core.events.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.base.AbstractCargoItem;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Table("cargo_item")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CargoItem extends AbstractCargoItem {

  @Id private UUID id;

  @Transient
  @Column("shipping_instruction_id")
  private UUID shippingInstructionID;

  @Column("utilized_transport_equipment_id")
  @NotNull
  protected UUID utilizedTransportEquipmentID;

  @Column("consignment_item_id")
  private UUID consignmentItemID;

  @Size(max = 50)
  @Column("package_name_on_bl")
  private String packageNameOnBL;
}
