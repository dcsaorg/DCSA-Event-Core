package org.dcsa.core.events.model.mapper;

import org.dcsa.core.events.model.Equipment;
import org.dcsa.core.events.model.transferobjects.EquipmentTO;
import org.dcsa.core.events.repository.ShipmentEquipmentCustomRepository;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {
	EquipmentTO equipmentToDTO(Equipment equipment);
	EquipmentTO shipmentEquipmentDetailsToDTO(ShipmentEquipmentCustomRepository.ShipmentEquipmentDetails shipmentEquipmentDetails);
	Equipment dtoToEquipment(EquipmentTO equipmentTO);
}
