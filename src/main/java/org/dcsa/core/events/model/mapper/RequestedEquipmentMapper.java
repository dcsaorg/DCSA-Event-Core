package org.dcsa.core.events.model.mapper;

import org.dcsa.core.events.edocumentation.model.transferobject.RequestedEquipmentTO;
import org.dcsa.core.events.model.*;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface RequestedEquipmentMapper {
  RequestedEquipmentTO requestedEquipmentToDTO(RequestedEquipment requestedEquipment);

  RequestedEquipment dtoToRequestedEquipment(RequestedEquipmentTO requestedEquipmentTO);

  @Mapping(source = "bookingId", target = "bookingID")
  RequestedEquipment dtoToRequestedEquipmentWithBookingId(RequestedEquipmentTO requestedEquipmentTO, UUID bookingId);

  @Mapping(source = "shipmentId", target = "shipmentID")
  RequestedEquipment dtoToRequestedEquipmentWithShipmentId(RequestedEquipmentTO requestedEquipmentTO, UUID shipmentId);
}
