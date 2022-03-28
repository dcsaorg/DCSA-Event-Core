package org.dcsa.core.events.edocumentation.model.mapper;

import org.dcsa.core.events.edocumentation.model.ConsignmentItem;
import org.dcsa.core.events.edocumentation.model.transferobject.ConsignmentItemTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConsignmentItemMapper {
  ConsignmentItemTO consignmentItemToDTO(ConsignmentItem consignmentItem);

  //	@Mapping(source = "utilizedTransportEquipmentID", target = "utilizedTransportEquipmentID")
  //	@Mapping(source = "isNewRecord", target = "newRecord")
  ConsignmentItem dtoToConsignmentItem(ConsignmentItemTO consignmentItemTO);
}
