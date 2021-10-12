package org.dcsa.core.events.model.transferobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.Address;
import org.dcsa.core.events.model.Facility;
import org.dcsa.core.events.model.Location;
import org.dcsa.core.events.model.base.AbstractLocation;
import org.dcsa.core.events.model.enums.FacilityCodeListProvider;
import org.dcsa.core.model.ForeignKey;
import org.dcsa.core.util.MappingUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.sql.Join;

import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class LocationTO extends AbstractLocation {

    private static final Address NULL_ADDRESS = new Address();
    private static final Facility NULL_FACILITY = new Facility();
    private static final LocationTO NULL_LOCATION = new LocationTO();
    private static final LocationTO NULL_LOCATION_WITH_NULL_ADDRESS_AND_FACILITY = new LocationTO();

    static {
        NULL_LOCATION_WITH_NULL_ADDRESS_AND_FACILITY.setAddress(NULL_ADDRESS);
        NULL_LOCATION_WITH_NULL_ADDRESS_AND_FACILITY.setFacility(NULL_FACILITY);
    }

    @ForeignKey(fromFieldName = "addressID", foreignFieldName = "id", joinType = Join.JoinType.LEFT_OUTER_JOIN)
    @Transient
    private Address address;

    @ForeignKey(fromFieldName = "facilityID", foreignFieldName = "facilityID", joinType = Join.JoinType.LEFT_OUTER_JOIN)
    @Transient
    @JsonIgnore
    // Use Facility (and not FacilityTO) to avoid infinite recursion with FacilityTO and because we do not need a
    // full FacilityTO object.
    private Facility facility;

    @Size(max = 6)
    @Transient
    private String facilityCode;

    @Transient
    private FacilityCodeListProvider facilityCodeListProvider;

    public void setFacility(Facility facility) {
        if (facility != null && !NULL_FACILITY.equals(facility)) {
            this.setFacilityID(facility.getFacilityID());
            if (this.getUnLocationCode() != null) {
                this.setUnLocationCode(facility.getUnLocationCode());
            }
            if (facility.getFacilitySMDGCode() != null) {
                facilityCode = facility.getFacilitySMDGCode();
                facilityCodeListProvider = FacilityCodeListProvider.SMDG;
            } else if (facility.getFacilityBICCode() != null) {
                facilityCode = facility.getFacilityBICCode();
                facilityCodeListProvider = FacilityCodeListProvider.BIC;
            } else {
                throw new IllegalArgumentException("Unsupported facility code list provider.");
            }
        } else {
            // Preserve UNLocationCode as it is - in many cases we have a UN Location Code without a facility
            facilityCode = null;
            facilityCodeListProvider = null;
        }
        this.facility = facility;
    }

    // Use ExtendedRequest + @ForeignKey instead
    @Deprecated
    public Location toLocation() {
        Location location = MappingUtils.instanceFrom(this, Location::new, AbstractLocation.class);
        if (this.address != null) {
            location.setAddressID(this.address.getId());
        }
        if (this.facility != null) {
            location.setFacilityID(facility.getFacilityID());
        }
        return location;
    }

    @JsonIgnore
    public boolean isNullLocation() {
        return this.equals(NULL_LOCATION_WITH_NULL_ADDRESS_AND_FACILITY) || this.equals(NULL_LOCATION);
    }
}
