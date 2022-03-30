package org.dcsa.core.events.model.mapper;

import org.dcsa.core.events.model.CargoItem;
import org.dcsa.core.events.model.transferobjects.CargoItemTO;
import org.dcsa.core.events.repository.CargoItemCustomRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CargoItemMapper {

	@Mapping(source = "utilizedTransportEquipmentID", target = "utilizedTransportEquipmentID")
	@Mapping(source = "shippingInstructionReference", target = "shippingInstructionReference")
	CargoItem dtoToCargoItem(CargoItemTO cargoItemTO, UUID utilizedTransportEquipmentID, String shippingInstructionReference);

	@Mapping(source = "consignmentId", target = "consignmentItemID")
	@Mapping(source = "shippingInstructionReference", target = "shippingInstructionReference")
	CargoItem dtoToCargoItemWithConsignmentIdAndShippingInstructionReference(CargoItemTO cargoItemTO, UUID consignmentId, String shippingInstructionReference);

	CargoItemTO cargoItemWithCargoLineItemsToDTO(CargoItemCustomRepository.CargoItemWithCargoLineItems cargoItemWithCargoLineItems);
}
