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

    @ForeignKey(foreignFieldName = "id", fromFieldName = "locationID", joinType = Join.JoinType.LEFT_OUTER_JOIN)
    @Transient
    @JsonIgnore /* It is mapped via TransportCallTO and its getLocation */
    private LocationTO location;

}
