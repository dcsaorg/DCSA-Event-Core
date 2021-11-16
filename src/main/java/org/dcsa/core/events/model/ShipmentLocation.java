package org.dcsa.core.events.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.enums.LocationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("shipment_location")
public class ShipmentLocation extends AbstractShipmentLocation {

}
