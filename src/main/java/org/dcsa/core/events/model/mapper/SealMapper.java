package org.dcsa.core.events.model.mapper;

import org.dcsa.core.events.model.Seal;
import org.dcsa.core.events.model.transferobjects.SealTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface SealMapper {

	SealTO sealToDTO(Seal seal);

	@Mapping(source = "shipmentEquipmentID", target = "shipmentEquipmentID")
	Seal dtoToSeal(SealTO sealTO, UUID shipmentEquipmentID);
}
