package org.dcsa.core.events.model.mapper;

import org.dcsa.core.events.model.Equipment;
import org.dcsa.core.events.model.transferobjects.EquipmentTO;
import org.dcsa.core.events.repository.UtilizedTransportEquipmentCustomRepository;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {
	EquipmentTO equipmentToDTO(Equipment equipment);
	EquipmentTO utilizedTransportEquipmentDetailsToDTO(UtilizedTransportEquipmentCustomRepository.UtilizedTransportEquipmentDetails utilizedTransportEquipmentDetails);
	Equipment dtoToEquipment(EquipmentTO equipmentTO);
}
