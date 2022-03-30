package org.dcsa.core.events.model.mapper;

import org.dcsa.core.events.model.UtilizedTransportEquipment;
import org.dcsa.core.events.model.transferobjects.UtilizedTransportEquipmentTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UtilizedTransportEquipmentMapper {

  @Mapping(source = "shipmentID", target = "shipmentID")
  @Mapping(source = "utilizedTransportEquipmentTO.equipment.equipmentReference", target = "equipmentReference")
  @Mapping(source = "utilizedTransportEquipmentTO.id", target = "id", ignore = true)
  UtilizedTransportEquipment dtoToUtilizedTransportEquipment(UtilizedTransportEquipmentTO utilizedTransportEquipmentTO, UUID shipmentID);
}
