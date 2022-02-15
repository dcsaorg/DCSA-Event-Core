package org.dcsa.core.events.edocumentation.model.mapper;

import org.dcsa.core.events.edocumentation.model.transferobject.ChargeTO;
import org.dcsa.core.events.model.Charge;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChargeMapper {
    ChargeTO chargeToDTO(Charge charge);
}
