package org.dcsa.core.events.edocumentation.model;

import lombok.Data;
import org.dcsa.core.events.model.enums.VolumeUnit;
import org.dcsa.core.events.model.enums.WeightUnit;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@Table("consignment_item")
public class ConsignmentItem {
  @Id
  private UUID id;

  @NotBlank
  @Column("description_of_goods")
  private String descriptionOfGoods;

  @NotNull
  @Size(max = 10)
  @Column("hs_code")
  private String hsCode;

  @NotNull
  @Size(max = 100)
  @Column("shipping_instruction_id")
  private String shippingInstructionID;

  @NotNull
  @Column("weight")
  private Double weight;

  @NotNull
  @Column("weight_unit")
  private WeightUnit weightUnit;

  @Column("volume")
  private Double volume;

  @Column("volume_unit")
  private VolumeUnit volumeUnit;

  @NotNull
  @Column("shipment_id")
  private UUID shipmentID;
}
