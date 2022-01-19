package org.dcsa.core.events.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@Table("requested_equipment")
public class RequestedEquipment {
  @Id private UUID id;

  @Column("booking_id")
  private UUID bookingID;

  @Column("shipment_id")
  private UUID shipmentID;

  @Size(max = 4)
  @Column("requested_equipment_sizetype")
  private String requestedEquipmentSizetype;

  @Column("requested_equipment_units")
  private Integer requestedEquipmentUnits;

  @Size(max = 4)
  @Column("confirmed_equipment_sizetype")
  private String confirmedEquipmentSizetype;

  @Column("confirmed_equipment_units")
  private Integer confirmedEquipmentUnits;

  @Column("is_shipper_owned")
  private Boolean isShipperOwned = false;
}
