package org.dcsa.core.events.edocumentation.model.mapper;

import org.dcsa.core.events.edocumentation.model.ConsignmentItem;
import org.dcsa.core.events.edocumentation.model.transferobject.ConsignmentItemTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ConsignmentItemMapper {
  ConsignmentItemTO consignmentItemToDTO(ConsignmentItem consignmentItem);

  @Mapping(source = "shippingInstructionReference", target = "shippingInstructionID")
  @Mapping(source = "shipmentId", target = "shipmentID")
  ConsignmentItem dtoToConsignmentItemWithShippingReferenceAndShipmentId(
      ConsignmentItemTO consignmentItemTO, String shippingInstructionReference, UUID shipmentId);

  ConsignmentItem dtoToConsignmentItem(ConsignmentItemTO consignmentItemTO);
}
