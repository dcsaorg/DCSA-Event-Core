package org.dcsa.core.events.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.base.AbstractShippingInstruction;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("shipping_instruction")
public class ShippingInstruction extends AbstractShippingInstruction {

  @Id
  @Column("id")
  private UUID id;
}
