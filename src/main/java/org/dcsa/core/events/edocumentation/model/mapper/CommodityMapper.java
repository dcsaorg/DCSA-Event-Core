package org.dcsa.core.events.edocumentation.model.mapper;

import org.dcsa.core.events.edocumentation.model.transferobject.CommodityTO;
import org.dcsa.core.events.model.Commodity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommodityMapper {
  CommodityTO commodityToDTO(Commodity commodity);

  Commodity dtoToCommodity(CommodityTO commodityTO);
}
