package org.dcsa.core.events.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.base.AbstractShippingInstruction;
import org.springframework.data.relational.core.mapping.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("shipping_instruction")
public class ShippingInstruction extends AbstractShippingInstruction {
}
