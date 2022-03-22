package org.dcsa.core.events.model;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Internal implementation detail used for the GenericEventServiceImpl
 */
@Data
@Table("event_carrier_booking_reference")
public class EventCarrierBookingReference {

  @Column("link_type")
  private String linkType;

  @Column("transport_call_id")
  private String transportCallID;

  @Column("document_id")
  private String documentID;

  @Column("carrier_booking_reference")
  private String carrierBookingReference;
}
