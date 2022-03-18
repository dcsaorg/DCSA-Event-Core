package org.dcsa.core.events.model.mapper;

import org.dcsa.core.events.model.Party;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PartyMapper {
  PartyTO partyToDTO(Party party);

  Party dtoToParty(PartyTO partyTO);
}
