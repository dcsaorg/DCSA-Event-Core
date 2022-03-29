package org.dcsa.core.events.model.mapper;

import org.dcsa.core.events.model.UtilizedTransportEquipment;
import org.dcsa.core.events.model.transferobjects.UtilizedTransportEquipmentTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UtilizedTransportEquipmentMapper {

	@Mapping(source = "utilizedTransportEquipmentTO.equipment.equipmentReference", target = "equipmentReference")
    UtilizedTransportEquipment dtoToUtilizedTransportEquipment(UtilizedTransportEquipmentTO utilizedTransportEquipmentTO);
}
