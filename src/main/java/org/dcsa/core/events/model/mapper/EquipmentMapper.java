package org.dcsa.core.events.model.mapper;

import org.dcsa.core.events.model.Equipment;
import org.dcsa.core.events.model.transferobjects.EquipmentTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {
	EquipmentTO equipmentToDTO(Equipment equipment);
	Equipment dtoToEquipment(EquipmentTO equipmentTO);
}
