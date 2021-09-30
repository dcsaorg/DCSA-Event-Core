package org.dcsa.core.events.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.base.AbstractLocation;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.events.service.FacilityService;
import org.dcsa.core.util.MappingUtils;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Location extends AbstractLocation {

    // Use LocationTO with ExtendedRequest and the @ForeignKey support.
    @Deprecated
    public LocationTO toLocationTO(Address address, Facility facility) {
        LocationTO locationTO;
        UUID providedAddressID = address != null ? address.getId() : null;
        UUID providedFacilityID = facility != null ? facility.getFacilityID() : null;
        if (!Objects.equals(getAddressID(), providedAddressID)) {
            throw new IllegalArgumentException("address does not match addressID");
        }
        if (!Objects.equals(getFacilityID(), providedFacilityID)) {
            throw new IllegalArgumentException("facility does not match FacilityID");
        }
        locationTO = MappingUtils.instanceFrom(this, LocationTO::new, AbstractLocation.class);
        locationTO.setAddress(address);
        locationTO.setFacility(facility);
        return locationTO;
    }
}
