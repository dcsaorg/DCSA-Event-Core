package org.dcsa.core.events.model.transferobjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.Transport;
import org.dcsa.core.events.model.Vessel;
import org.dcsa.core.events.model.base.AbstractTransportCall;
import org.dcsa.core.events.model.enums.FacilityCodeListProvider;
import org.dcsa.core.model.ForeignKey;
import org.dcsa.core.model.JoinedWithModel;
import org.dcsa.core.model.MapEntity;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.sql.Join;

import javax.validation.constraints.Size;
import java.util.Objects;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
// TODO: We need to do DISTINCT + (dischargeTransportCallID OR loadTransportCallID)
// - Note LEFT OUTER JOIN because not all transport calls involve vessels.
@JoinedWithModel(lhsFieldName = "transportCallID", rhsModel = Transport.class, rhsFieldName = "dischargeTransportCallID")
@JoinedWithModel(lhsModel = Transport.class, lhsFieldName = "vesselIMONumber", rhsModel = Vessel.class, rhsFieldName = "vesselIMONumber", joinType = Join.JoinType.LEFT_OUTER_JOIN)
public class TransportCallTO extends AbstractTransportCall {

    private static final Vessel NULL_VESSEL = new Vessel();

    @Size(max = 5)
    @Transient
    @JsonProperty("UNLocationCode")
    private String UNLocationCode;

    @Size(max = 6)
    @Transient
    private String facilityCode;

    @Transient
    private FacilityCodeListProvider facilityCodeListProvider;

    @ForeignKey(fromFieldName = "facilityID", foreignFieldName = "facilityID")
    @Transient
    private FacilityTO facility;

    @MapEntity
    @Transient
    private Vessel vessel;

    public void setVessel(Vessel vessel) {
        if (Objects.equals(NULL_VESSEL, vessel)) {
            // Due to LEFT OUTER JOIN and @MapEntity, we can see a vessel consisting entirely of nulls.
            // Map that to a regular null (the vessel field is optional, but if it is present, then we
            // have mandatory "not null" fields to supply).
            vessel = null;
        }
        this.vessel = vessel;
    }

    @Transient
    private LocationTO location;

    public LocationTO getLocation() {
        if (location != null && !location.isNullLocation()) {
            return location;
        }
        if (facility != null) {
            LocationTO facilityLocation = facility.getLocation();
            if (facilityLocation != null && !facilityLocation.isNullLocation()) {
                return facilityLocation;
            }
        }
        return null;
    }

    public void setFacility(FacilityTO facility) {
        if (facility != null) {
            UNLocationCode = facility.getUnLocationCode();
            if (facility.getFacilitySMGDCode() != null) {
                facilityCode = facility.getFacilitySMGDCode();
                facilityCodeListProvider = FacilityCodeListProvider.SMDG;
            } else {
                facilityCode = facility.getFacilityBICCode();
                facilityCodeListProvider = FacilityCodeListProvider.BIC;
            }
        } else {
            facilityCode = null;
            facilityCodeListProvider = null;
            UNLocationCode = null;
        }
        this.facility = facility;
    }
}
