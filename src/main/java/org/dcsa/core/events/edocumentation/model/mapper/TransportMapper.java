package org.dcsa.core.events.edocumentation.model.mapper;

import org.dcsa.core.events.edocumentation.model.transferobject.TransportTO;
import org.dcsa.core.events.model.Transport;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransportMapper {
  TransportTO transportToDTO(Transport transport);
}
