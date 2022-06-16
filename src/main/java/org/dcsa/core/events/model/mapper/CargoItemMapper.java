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
  @Mapping(source = "shippingInstructionID", target = "shippingInstructionID")
  CargoItem dtoToCargoItem(
      CargoItemTO cargoItemTO, UUID utilizedTransportEquipmentID, UUID shippingInstructionID);

  @Mapping(source = "equipmentReference", target = "equipmentReference")
  CargoItemTO cargoItemToDto(CargoItem cargoItem, String equipmentReference);

  @Mapping(source = "consignmentId", target = "consignmentItemID")
  @Mapping(source = "shippingInstructionID", target = "shippingInstructionID")
  CargoItem dtoToCargoItemWithConsignmentIdAndShippingInstructionID(
      CargoItemTO cargoItemTO, UUID consignmentId, UUID shippingInstructionID);

  CargoItemTO cargoItemWithCargoLineItemsToDTO(
      CargoItemCustomRepository.CargoItemWithCargoLineItems cargoItemWithCargoLineItems);
}
