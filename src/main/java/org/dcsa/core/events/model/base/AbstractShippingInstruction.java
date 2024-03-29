package org.dcsa.core.events.model.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.enums.ShipmentEventTypeCode;
import org.dcsa.core.events.model.enums.TransportDocumentTypeCode;
import org.dcsa.core.model.AuditBase;
import org.dcsa.core.validator.EnumSubset;
import org.springframework.data.relational.core.mapping.Column;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.dcsa.core.events.model.enums.ShipmentEventTypeCode.EBL_DOCUMENT_STATUSES;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class AbstractShippingInstruction extends AuditBase {

  @Size(max = 100)
  @Column("shipping_instruction_reference")
  private String shippingInstructionReference;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @EnumSubset(anyOf = EBL_DOCUMENT_STATUSES)
  @Column("document_status")
  private ShipmentEventTypeCode documentStatus;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column("created_date_time")
  private OffsetDateTime shippingInstructionCreatedDateTime;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column("updated_date_time")
  private OffsetDateTime shippingInstructionUpdatedDateTime;

  @NotNull
  @Column("is_shipped_onboard_type")
  private Boolean isShippedOnBoardType;

  @Column("number_of_copies")
  private Integer numberOfCopies;

  @Column("number_of_originals")
  private Integer numberOfOriginals;

  @NotNull
  @Column("is_electronic")
  private Boolean isElectronic;

  @NotNull
  @Column("is_to_order")
  private Boolean isToOrder;

  @Column("are_charges_displayed_on_originals")
  private Boolean areChargesDisplayedOnOriginals;

  @Column("are_charges_displayed_on_copies")
  private Boolean areChargesDisplayedOnCopies;

  @Column("place_of_issue_id")
  private UUID placeOfIssueID;

  @Column("transport_document_type_code")
  private TransportDocumentTypeCode transportDocumentTypeCode;

  @Size(max = 250)
  @Column("displayed_name_for_place_of_receipt")
  private String displayedNameForPlaceOfReceipt;

  @Size(max = 250)
  @Column("displayed_name_for_port_of_load")
  private String displayedNameForPortOfLoad;

  @Size(max = 250)
  @Column("displayed_name_for_port_of_discharge")
  private String displayedNameForPortOfDischarge;

  @Size(max = 250)
  @Column("displayed_name_for_place_of_delivery")
  private String displayedNameForPlaceOfDelivery;

  @Column("amendment_to_transport_document_id")
  private UUID amendmentToTransportDocument;

  @Column("valid_until")
  private OffsetDateTime validUntil;
}
