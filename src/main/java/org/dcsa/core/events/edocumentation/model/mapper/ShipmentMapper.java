package org.dcsa.core.events.edocumentation.model.mapper;

import org.dcsa.core.events.edocumentation.model.transferobject.ShipmentCutOffTimeTO;
import org.dcsa.core.events.edocumentation.model.transferobject.ShipmentLocationTO;
import org.dcsa.core.events.edocumentation.model.transferobject.ShipmentTO;
import org.dcsa.core.events.model.Shipment;
import org.dcsa.core.events.model.ShipmentCutOffTime;
import org.dcsa.core.events.model.ShipmentLocation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShipmentMapper {

    @Mapping(source = "updatedDateTime", target = "shipmentUpdatedDateTime")
    @Mapping(source = "confirmationDateTime", target = "shipmentCreatedDateTime")
    ShipmentTO shipmentToDTO(Shipment shipment);
    ShipmentCutOffTimeTO shipmentCutOffTimeToDTO(ShipmentCutOffTime shipmentCutOffTime);
    ShipmentLocationTO shipmentLocationToDTO(ShipmentLocation shipmentLocation);
}
