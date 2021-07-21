package org.dcsa.core.events.model.transferobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.Address;
import org.dcsa.core.events.model.Location;
import org.dcsa.core.events.model.base.AbstractLocation;
import org.dcsa.core.model.ForeignKey;
import org.dcsa.core.util.MappingUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.sql.Join;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class LocationTO extends AbstractLocation {

    public static final LocationTO NULL_LOCATION = new LocationTO();
    public static final LocationTO NULL_LOCATION_WITH_NULL_ADDRESS = new LocationTO();

    static {
        NULL_LOCATION_WITH_NULL_ADDRESS.setAddress(Address.NULL_ADDRESS);
    }

    @ForeignKey(fromFieldName = "addressID", foreignFieldName = "id", joinType = Join.JoinType.LEFT_OUTER_JOIN)
    @Transient
    private Address address;

    // Use ExtendedRequest + @ForeignKey instead
    @Deprecated
    public Location toLocation() {
        Location location = MappingUtils.instanceFrom(this, Location::new, AbstractLocation.class);
        if (this.address != null) {
            location.setAddressID(this.address.getId());
        }
        return location;
    }

    @JsonIgnore
    public boolean isNullLocation() {
        return this.equals(NULL_LOCATION_WITH_NULL_ADDRESS) || this.equals(NULL_LOCATION);
    }
}
