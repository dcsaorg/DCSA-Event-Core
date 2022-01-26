package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.enums.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Table("booking")
@NoArgsConstructor
@Data
public class Booking implements Persistable<UUID> {

  @Id
  @Column("id")
  private UUID id;

  @Size(max = 100)
  @Column("carrier_booking_request_reference")
  private String carrierBookingRequestReference;

  @Column("document_status")
  private DocumentStatus documentStatus;

  @Column("receipt_type_at_origin")
  private ReceiptDeliveryType receiptTypeAtOrigin;

  @Column("delivery_type_at_destination")
  private ReceiptDeliveryType deliveryTypeAtDestination;

  @Column("cargo_movement_type_at_origin")
  private CargoMovementType cargoMovementTypeAtOrigin;

  @Column("cargo_movement_type_at_destination")
  private CargoMovementType cargoMovementTypeAtDestination;

  @Column("booking_request_datetime")
  private OffsetDateTime bookingRequestDateTime;

  @Column("service_contract_reference")
  @Size(max = 30)
  private String serviceContractReference;

  @Column("payment_term_code")
  private PaymentTerm paymentTermCode;

  @Column("is_partial_load_allowed")
  private Boolean isPartialLoadAllowed;

  @Column("is_export_declaration_required")
  private Boolean isExportDeclarationRequired;

  @Column("export_declaration_reference")
  @Size(max = 35)
  private String exportDeclarationReference;

  @Column("is_import_license_required")
  private Boolean isImportLicenseRequired;

  @Column("import_license_reference")
  @Size(max = 35)
  private String importLicenseReference;

  @Column("submission_datetime")
  private OffsetDateTime submissionDateTime;

  @Column("is_ams_aci_filing_required")
  private Boolean isAMSACIFilingRequired;

  @Column("is_destination_filing_required")
  private Boolean isDestinationFilingRequired;

  @Column("contract_quotation_reference")
  @Size(max = 35)
  private String contractQuotationReference;

  @Column("incoterms")
  private IncoTerms incoTerms;

  @Column("invoice_payable_at")
  @Size(max = 100)
  private String invoicePayableAt;

  @Column("expected_departure_date")
  private LocalDate expectedDepartureDate;

  @Column("transport_document_type_code")
  private TransportDocumentTypeCode transportDocumentTypeCode;

  @Column("transport_document_reference")
  private String transportDocumentReference;

  @Column("booking_channel_reference")
  @Size(max = 20)
  private String bookingChannelReference;

  @Column("communication_channel_code")
  private CommunicationChannel communicationChannelCode;

  @Column("is_equipment_substitution_allowed")
  private Boolean isEquipmentSubstitutionAllowed;

  @Column("vessel_id")
  private UUID vesselId;

  @Column("export_voyage_number")
  @Size(max = 50)
  private String exportVoyageNumber;

  @Column("place_of_issue")
  @Size(max = 100)
  private String placeOfIssueID;

  @JsonIgnore
  @Column("pre_carriage_mode_of_transport_code")
  @Size(max = 3)
  protected String preCarriageModeOfTransportCode;

  // updatedDateTime is metadata to avoid having to query shipment_event for updated dateTime.
  // This is not part of the official IM model. They are added in the sql only.
  @Column("updated_date_time")
  protected OffsetDateTime updatedDateTime;

  @Override
  public boolean isNew() {
    return this.getId() == null;
  }
}