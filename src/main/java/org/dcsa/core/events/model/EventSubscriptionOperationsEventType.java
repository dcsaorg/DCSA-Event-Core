package org.dcsa.core.events.model;

import lombok.Data;
import org.dcsa.core.events.model.enums.OperationsEventTypeCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("event_subscription_operations_event_type")
public class EventSubscriptionOperationsEventType {

  @Column("subscription_id")
  private UUID subscriptionID;

  @Column("operations_event_type_code")
  private OperationsEventTypeCode operationsEventTypeCode;
}
