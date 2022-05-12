package org.dcsa.core.events.edocumentation.model.mapper;

import org.dcsa.core.events.edocumentation.model.transferobject.ShipmentLocationTO;
import org.dcsa.core.events.model.ShipmentLocation;
import org.dcsa.skernel.model.transferobjects.LocationTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ShipmentLocationMapper {

  @Mapping(source = "bookingId", target = "bookingID")
  ShipmentLocation dtoToShipmentLocationWithBookingID(ShipmentLocationTO shipmentLocationTO, UUID bookingId);

  @Mapping(source = "locationTO", target = "locationTO")
  ShipmentLocationTO shipmentLocationToDtoWithLocationTO(ShipmentLocation shipmentLocation, LocationTO locationTO);
}
