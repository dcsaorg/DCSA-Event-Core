package org.dcsa.core.events.edocumentation.model.mapper;

import org.dcsa.core.events.edocumentation.model.transferobject.ConfirmedEquipmentTO;
import org.dcsa.core.events.model.RequestedEquipment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConfirmedEquipmentMapper {
	ConfirmedEquipmentTO requestedEquipmentToDto(RequestedEquipment requestedEquipment);
}
