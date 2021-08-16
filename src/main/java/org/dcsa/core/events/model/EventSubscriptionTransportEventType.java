package org.dcsa.core.events.model;

import lombok.Data;
import org.dcsa.core.events.model.enums.TransportEventTypeCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("event_subscription_transport_event_type")
public class EventSubscriptionTransportEventType {

  @Column("subscription_id")
  private UUID subscriptionID;

  @Column("transport_event_type_code")
  private TransportEventTypeCode transportEventTypeCode;
}
