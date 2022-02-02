package org.dcsa.core.events.model.mappers;

import org.dcsa.core.events.model.Address;
import org.dcsa.core.events.model.Facility;
import org.dcsa.core.events.model.Location;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LocationMapper {
  LocationTO locationToDTO(Location location);

  @Mapping(source = "location.id", target = "id")
  @Mapping(source = "location.unLocationCode", target = "unLocationCode")
  @Mapping(source = "location.facilityID", target = "facilityID")
  LocationTO locationToDTO(Location location, Address address, Facility facility);

  Location dtoToLocation(LocationTO locationTO);
}
