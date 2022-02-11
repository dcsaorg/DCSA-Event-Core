package org.dcsa.core.events.model.mapper;

import org.dcsa.core.events.model.CargoItem;
import org.dcsa.core.events.model.transferobjects.CargoItemTO;
import org.dcsa.core.events.repository.CargoItemCustomRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CargoItemMapper {

	@Mapping(source = "shipmentEquipmentID", target = "shipmentEquipmentID")
	@Mapping(source = "shippingInstructionID", target = "shippingInstructionID")
	CargoItem dtoToCargoItem(CargoItemTO cargoItemTO, UUID shipmentEquipmentID, String shippingInstructionID);

	CargoItemTO cargoItemWithCargoLineItemsToDTO(CargoItemCustomRepository.CargoItemWithCargoLineItems cargoItemWithCargoLineItems);
}
