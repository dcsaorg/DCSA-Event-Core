package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.enums.ReferenceTypeCode;
import org.dcsa.core.model.AuditBase;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Table("reference")
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class Reference extends AuditBase {

  @Id
  @Column("id")
  @JsonIgnore
  private UUID referenceID;

  @Column("reference_type_code")
  @NotNull
  private ReferenceTypeCode referenceType;

  @Size(max = 100)
  @NotNull
  private String referenceValue;

  @Column("shipping_instruction_id")
  @JsonIgnore
  private UUID shippingInstructionID;

  @Column("shipment_id")
  @JsonIgnore
  private String shipmentID;

  @Column("booking_id")
  @JsonIgnore
  private UUID bookingID;

  @Column("consignment_item_id")
  private UUID consignmentItemID;

  @Transient
  @Column("cargo_item_id")
  @JsonIgnore
  private UUID cargoItemID;

  @JsonIgnore
  public UUID getId() {
    return getReferenceID();
  }
}
