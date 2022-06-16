package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.dcsa.core.events.model.enums.TransportPlanStageCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("shipment_transport")
public class ShipmentTransport {

  @Id
  @Column("id")
  @JsonIgnore
  private UUID id;

  @JsonIgnore
  @Column("shipment_id")
  private UUID shipmentID;

  @JsonIgnore
  @Column("transport_id")
  private UUID transportID;

  @Column("transport_plan_stage_sequence_number")
  private Integer transportPlanStageSequenceNumber;

  @Column("transport_plan_stage_code")
  private TransportPlanStageCode transportPlanStageCode;

  @JsonIgnore
  @Column("commercial_voyage_id")
  private UUID commercialVoyageID;

  @Column("is_under_shippers_responsibility")
  private Boolean isUnderShippersResponsibility;
}
