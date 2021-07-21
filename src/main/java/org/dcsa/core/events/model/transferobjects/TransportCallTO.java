package org.dcsa.core.events.model.transferobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@JoinedWithModel(lhsFieldName = "transportCallID", rhsModel = Transport.class, rhsFieldName = "dischargeTransportCallID", rhsJoinAlias = "dischargeTransport", joinType = Join.JoinType.LEFT_OUTER_JOIN)
@JoinedWithModel(lhsJoinAlias = "dischargeTransport", lhsModel = Transport.class, lhsFieldName = "vesselIMONumber", rhsModel = Vessel.class, rhsFieldName = "vesselIMONumber", rhsJoinAlias = "discharge_vessel", joinType = Join.JoinType.LEFT_OUTER_JOIN)
@JoinedWithModel(lhsFieldName = "transportCallID", rhsModel = Transport.class, rhsFieldName = "loadTransportCallID", rhsJoinAlias = "loadTransport", joinType = Join.JoinType.LEFT_OUTER_JOIN)
@JoinedWithModel(lhsJoinAlias = "loadTransport", lhsModel = Transport.class, lhsFieldName = "vesselIMONumber", rhsModel = Vessel.class, rhsFieldName = "vesselIMONumber", rhsJoinAlias = "load_vessel", joinType = Join.JoinType.LEFT_OUTER_JOIN)
public class TransportCallTO extends AbstractTransportCall {

    @Size(max = 5)
    @Transient
    @JsonProperty("UNLocationCode")
    private String UNLocationCode;

    @Size(max = 6)
    @Transient
    private String facilityCode;

    @Transient
    private FacilityCodeListProvider facilityCodeListProvider;

    @ForeignKey(fromFieldName = "facilityID", foreignFieldName = "facilityID", joinType = Join.JoinType.LEFT_OUTER_JOIN)
    @JsonIgnore
    @Transient
    private FacilityTO facility;

    @MapEntity(joinAlias = "discharge_vessel")
    @JsonIgnore
    @Transient
    private Vessel dischargeVessel;

    @MapEntity(joinAlias = "load_vessel")
    @JsonIgnore
    @Transient
    private Vessel loadVessel;

    private Vessel vesselOrNull(Vessel vessel) {
        if (Objects.equals(Vessel.NULL_VESSEL, vessel)) {
            // Due to LEFT OUTER JOIN and @MapEntity, we can see a vessel consisting entirely of nulls.
            // Map that to a regular null (the vessel field is optional, but if it is present, then we
            // have mandatory "not null" fields to supply).
            return null;
        }
        return vessel;
    }

    public void setDischargeVessel(Vessel vessel) {
        this.dischargeVessel = vesselOrNull(vessel);
    }

    public void setLoadVessel(Vessel vessel) {
        this.loadVessel = vesselOrNull(vessel);
    }

    @Transient
    private Vessel vessel;

    public Vessel getVessel() {
        if (dischargeVessel == null) {
            return loadVessel;
        }
        return dischargeVessel;
    }

    public void setVessel(Vessel vessel) {
        this.vessel = vesselOrNull(vessel);
    }

    @ForeignKey(fromFieldName = "locationID", foreignFieldName = "id", joinType = Join.JoinType.LEFT_OUTER_JOIN)
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
        if (facility != null && !facility.isNullFacility()) {
            UNLocationCode = facility.getUnLocationCode();
            if (facility.getFacilitySMGDCode() != null) {
                facilityCode = facility.getFacilitySMGDCode();
                facilityCodeListProvider = FacilityCodeListProvider.SMDG;
            } else if (facility.getFacilityBICCode() != null) {
                facilityCode = facility.getFacilityBICCode();
                facilityCodeListProvider = FacilityCodeListProvider.BIC;
            } else {
                throw new IllegalArgumentException("Unsupported facility code list provider.");
            }
        } else {
            facilityCode = null;
            facilityCodeListProvider = null;
            UNLocationCode = null;
        }
        this.facility = facility;
    }
}
