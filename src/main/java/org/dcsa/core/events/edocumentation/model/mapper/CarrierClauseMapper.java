package org.dcsa.core.events.edocumentation.model.mapper;

import org.dcsa.core.events.edocumentation.model.transferobject.CarrierClauseTO;
import org.dcsa.core.events.model.CarrierClause;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarrierClauseMapper {
  CarrierClauseTO carrierClauseToDTO(CarrierClause carrierClause);
}
