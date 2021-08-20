package org.dcsa.core.events.model;

import lombok.Data;
import org.dcsa.core.events.model.enums.TransportDocumentTypeCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("event_subscription_transport_document_type")
public class EventSubscriptionTransportDocumentEventType {

  @Column("subscription_id")
  private UUID subscriptionID;

  @Column("transport_document_type_code")
  private TransportDocumentTypeCode transportDocumentTypeCode;
}
