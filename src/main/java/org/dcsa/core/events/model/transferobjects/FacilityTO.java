package org.dcsa.core.events.model.transferobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.base.AbstractFacility;
import org.dcsa.core.model.ForeignKey;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.sql.Join;

@Data
@EqualsAndHashCode(callSuper = true)
public class FacilityTO extends AbstractFacility {

    public static FacilityTO NULL_FACILITY = new FacilityTO();
    public static FacilityTO NULL_FACILITY_WITH_NULL_LOCATION_WITH_NULL_ADDRESS = new FacilityTO();

    static {
        NULL_FACILITY_WITH_NULL_LOCATION_WITH_NULL_ADDRESS.setLocation(LocationTO.NULL_LOCATION_WITH_NULL_ADDRESS);
    }

    @ForeignKey(foreignFieldName = "id", fromFieldName = "locationID", joinType = Join.JoinType.LEFT_OUTER_JOIN)
    @Transient
    @JsonIgnore /* It is mapped via TransportCallTO and its getLocation */
    private LocationTO location;

    @JsonIgnore
    public boolean isNullFacility() {
        return this.equals(NULL_FACILITY_WITH_NULL_LOCATION_WITH_NULL_ADDRESS) || this.equals(NULL_FACILITY);
    }
}
