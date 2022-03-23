package org.dcsa.core.events.model;

import lombok.Data;
import org.dcsa.core.events.model.enums.ValueAddedServiceCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@Table("value_added_service_request")
public class ValueAddedServiceRequest {

  @Id private UUID id;

  @Column("booking_id")
  private UUID bookingID;

  // TODO: use shippingInstructionId when implemented
  @Size(max = 100)
  @Column("shipping_instruction_id")
  private String shippingInstructionID;

  @Column("value_added_service_code")
  private ValueAddedServiceCode valueAddedServiceCode;
}
