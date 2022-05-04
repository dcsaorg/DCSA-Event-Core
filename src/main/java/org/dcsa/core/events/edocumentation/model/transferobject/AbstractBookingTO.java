package org.dcsa.core.events.edocumentation.model.transferobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.dcsa.core.events.model.enums.*;
import org.dcsa.core.validator.EnumSubset;
import org.dcsa.skernel.validator.ValidVesselIMONumber;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.dcsa.core.events.model.enums.ShipmentEventTypeCode.BOOKING_DOCUMENT_STATUSES;

@Data
abstract class AbstractBookingTO {

  protected String carrierBookingRequestReference;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @EnumSubset(anyOf = BOOKING_DOCUMENT_STATUSES)
  protected ShipmentEventTypeCode documentStatus;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  protected OffsetDateTime bookingRequestCreatedDateTime;

  @NotNull(message = "The attribute receiptTypeAtOrigin is required.")
  protected ReceiptDeliveryType receiptTypeAtOrigin;

  @NotNull(message = "The attribute deliveryTypeAtDestination is required.")
  protected ReceiptDeliveryType deliveryTypeAtDestination;

  @NotNull(message = "The attribute cargoMovementTypeAtOrigin is required.")
  protected CargoMovementType cargoMovementTypeAtOrigin;

  @NotNull(message = "The attribute cargoMovementTypeAtDestination is required.")
  protected CargoMovementType cargoMovementTypeAtDestination;

  @NotBlank(message = "The attribute serviceContractReference is required.")
  @Size(max = 30, message = "The attribute serviceContractReference has a max size of 30.")
  protected String serviceContractReference;

  protected PaymentTerm paymentTermCode;

  @NotNull(message = "The attribute isPartialLoadAllowed is required.")
  protected Boolean isPartialLoadAllowed;

  @NotNull(message = "The attribute isExportDeclarationRequired is required.")
  protected Boolean isExportDeclarationRequired;

  protected String exportDeclarationReference;

  @NotNull(message = "The attribute isImportLicenseRequired is required.")
  protected Boolean isImportLicenseRequired;

  @Size(max = 35, message = "The attribute importLicenseReference has a max size of 35.")
  protected String importLicenseReference;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @NotNull(message = "The attribute submissionDateTime is required.")
  protected OffsetDateTime submissionDateTime;

  protected Boolean isAMSACIFilingRequired;

  protected Boolean isDestinationFilingRequired;

  @Size(max = 35, message = "The attribute contractQuotationReference has a max size of 35.")
  protected String contractQuotationReference;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  protected LocalDate expectedDepartureDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  protected LocalDate expectedArrivalAtPlaceOfDeliveryStartDate;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  protected LocalDate expectedArrivalAtPlaceOfDeliveryEndDate;

  protected TransportDocumentTypeCode transportDocumentTypeCode;

  @Size(max = 20, message = "The attribute transportDocumentReference has a max size of 20.")
  protected String transportDocumentReference;

  @Size(max = 20, message = "The attribute bookingChannelReference has a max size of 20.")
  protected String bookingChannelReference;

  protected IncoTerms incoTerms;

  @NotNull(message = "The attribute communicationChannelCode is required.")
  protected CommunicationChannelCode communicationChannelCode;

  @NotNull(message = "The attribute isEquipmentSubstitutionAllowed is required.")
  protected Boolean isEquipmentSubstitutionAllowed;

  @Size(max = 35, message = "The attribute vesselName has a max size of 35.")
  protected String vesselName;

  @ValidVesselIMONumber(allowNull = true, message = "The attribute vesselIMONumber is invalid.")
  protected String vesselIMONumber;

  @Size(max = 50, message = "The attribute exportVoyageNumber has a max size of 50.")
  protected String exportVoyageNumber;

  protected DCSATransportType preCarriageModeOfTransportCode;

  protected OffsetDateTime bookingRequestUpdatedDateTime;
}
