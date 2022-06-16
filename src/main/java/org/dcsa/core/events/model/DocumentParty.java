package org.dcsa.core.events.model;

import lombok.Data;
import org.dcsa.skernel.model.enums.PartyFunction;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("document_party")
public class DocumentParty {
  @Id private UUID id;

  @Column("party_id")
  private String partyID;

  @Column("shipping_instruction_id")
  private UUID shippingInstructionID;

  @Column("shipment_id")
  private UUID shipmentID;

  @Column("party_function")
  private PartyFunction partyFunction;

  @Column("is_to_be_notified")
  private Boolean isToBeNotified = false;

  @Column("booking_id")
  private UUID bookingID;
}
