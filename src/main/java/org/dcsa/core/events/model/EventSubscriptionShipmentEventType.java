package org.dcsa.core.events.model;

import lombok.Data;
import org.dcsa.core.events.model.enums.ShipmentEventTypeCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("event_subscription_shipment_event_type")
public class EventSubscriptionShipmentEventType {

  @Column("subscription_id")
  private UUID subscriptionID;

  @Column("shipment_event_type_code")
  private ShipmentEventTypeCode shipmentEventTypeCode;
}
