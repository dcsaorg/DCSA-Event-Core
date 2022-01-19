package org.dcsa.core.events.model;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Size;
import java.util.UUID;

@Table("shipment_carrier_clauses")
@Data
public class ShipmentCarrierClause {

  @Column("carrier_clause_id")
  private UUID carrierClauseID;

  @Column("shipment_id")
  private UUID shipmentID;

  @Size(max = 20)
  @Column("transport_document_reference ")
  private String transportDocumentReference;
}
