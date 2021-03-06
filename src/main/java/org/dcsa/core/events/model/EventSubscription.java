package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.base.AbstractEventSubscription;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.events.model.enums.SignatureMethod;
import org.dcsa.skernel.validator.ValidVesselIMONumber;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.List;

@Table("event_subscription")
@Data
@EqualsAndHashCode(callSuper = true)
public class EventSubscription extends AbstractEventSubscription implements EventSubscriptionState {

  @Column("carrier_booking_reference")
  private String carrierBookingReference;

  @Column("equipment_reference")
  private String equipmentReference;

  @Column("carrier_service_code")
  private String carrierServiceCode;

  @Column("carrier_voyage_number")
  private String carrierVoyageNumber;

  @ValidVesselIMONumber
  @Column("vessel_imo_number")
  private String vesselIMONumber;

  @Column("transport_document_reference")
  private String transportDocumentReference;

  // For historical reasons, we use TransportCallID externally (API) but TransportCallReference internally
  // Side-effect of DDT-1037
  @JsonProperty("transportCallID")
  @Column("transport_call_reference")
  private String transportCallReference;

  @Column("retry_count")
  private Long retryCount = 0L;

  @Column("retry_after")
  private OffsetDateTime retryAfter;

  @Column("accumulated_retry_delay")
  private Long accumulatedRetryDelay;

  @Column("last_bundle_size")
  private Integer lastBundleSize;

  @Column("signature_method")
  private SignatureMethod signatureMethod;

  public byte[] getSigningKey() {
    return secret;
  }

  public void copyInternalFieldsFrom(EventSubscription eventSubscription) {
    this.retryCount = eventSubscription.retryCount;
    this.retryAfter = eventSubscription.retryAfter;
    this.accumulatedRetryDelay = eventSubscription.accumulatedRetryDelay;
    this.signatureMethod = eventSubscription.signatureMethod;
  }

  @Override
  public List<EventType> getEventType() {
    throw new UnsupportedOperationException("This operation is not supported.");
  }
}
