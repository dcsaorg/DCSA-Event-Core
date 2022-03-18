package org.dcsa.core.events.model.mapper;

import org.dcsa.core.events.model.ShipmentEquipment;
import org.dcsa.core.events.model.transferobjects.ShipmentEquipmentTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ShipmentEquipmentMapper {


//	ShipmentEquipmentTO shipmentEquipmentToDTO(ShipmentEquipment shipmentEquipment);

	@Mapping(source = "shipmentID", target = "shipmentID")
	@Mapping(source = "shipmentEquipmentTO.equipment.equipmentReference", target = "equipmentReference")
	ShipmentEquipment dtoToShipmentEquipment(ShipmentEquipmentTO shipmentEquipmentTO, UUID shipmentID);
}
