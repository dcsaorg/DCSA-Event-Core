package org.dcsa.core.events.model;

import lombok.Data;
import org.dcsa.core.events.model.enums.PaymentTerm;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Size;
import java.util.UUID;

@Table("charge")
@Data
public class Charge {

  @Id
  @Size(max = 100)
  private String id;

  @Column("transport_document_reference")
  private String transportDocumentReference;

  @Column("shipment_id")
  private UUID shipmentID;

  @Column("charge_type")
  private String chargeType;

  @Column("currency_amount")
  private Double currencyAmount;

  @Column("currency_code")
  private String currencyCode;

  @Column("payment_term_code")
  private PaymentTerm paymentTermCode;

  @Column("calculation_basis")
  private String calculationBasis;

  @Column("unit_price")
  private Double unitPrice;

  @Column("quantity")
  private Double quantity;
}
