package org.dcsa.core.events.model.mapper;

import org.dcsa.core.events.model.CargoLineItem;
import org.dcsa.core.events.model.transferobjects.CargoLineItemTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CargoLineItemMapper {

	CargoLineItemTO cargoLineItemToDTO(CargoLineItem cargoLineItem);

	@Mapping(source = "cargoItemID", target = "cargoItemID")
	CargoLineItem dtoToCargoLineItem(CargoLineItemTO cargoLineItemTO, UUID cargoItemID);
}
