package org.dcsa.core.events.model;

import lombok.Data;
import org.dcsa.core.events.model.enums.EquipmentEventTypeCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("event_subscription_equipment_event_type")
public class EventSubscriptionEquipmentEventType {

  @Column("subscription_id")
  private UUID subscriptionID;

  @Column("equipment_event_type_code")
  private EquipmentEventTypeCode equipmentEventTypeCode;
}
