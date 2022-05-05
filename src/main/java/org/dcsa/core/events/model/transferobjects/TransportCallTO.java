package org.dcsa.core.events.model.transferobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.ModeOfTransport;
import org.dcsa.core.events.model.Service;
import org.dcsa.skernel.model.Vessel;
import org.dcsa.core.events.model.Voyage;
import org.dcsa.core.events.model.base.AbstractTransportCall;
import org.dcsa.core.events.model.enums.DCSATransportType;
import org.dcsa.core.model.ForeignKey;
import org.dcsa.skernel.model.enums.FacilityCodeListProvider;
import org.dcsa.skernel.model.transferobjects.FacilityTO;
import org.dcsa.skernel.model.transferobjects.LocationTO;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.sql.Join;

import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TransportCallTO extends AbstractTransportCall {

    @Size(max = 5)
    @Transient
    @JsonProperty("UNLocationCode")
    private String UNLocationCode;

    public String getUNLocationCode() {
        if (UNLocationCode == null && location != null) {
            return location.getUnLocationCode();
        }
        return UNLocationCode;
    }

    @Size(max = 6)
    @Transient
    private String facilityCode;

    @Transient
    private FacilityCodeListProvider facilityCodeListProvider;

    @ForeignKey(fromFieldName = "facilityID", foreignFieldName = "facilityID", joinType = Join.JoinType.LEFT_OUTER_JOIN)
    @JsonIgnore
    @Transient
    private FacilityTO facility;

    private Vessel vesselOrNull(Vessel vessel) {
        if (vessel.isNullVessel()) {
            // Due to LEFT OUTER JOIN and @MapEntity, we can see a vessel consisting entirely of nulls.
            // Map that to a regular null (the vessel field is optional, but if it is present, then we
            // have mandatory "not null" fields to supply).
            return null;
        }
        return vessel;
    }

    @Transient
    @ForeignKey(fromFieldName = "vesselID", foreignFieldName = "id", joinType = Join.JoinType.LEFT_OUTER_JOIN)
    private Vessel vessel;

    public void setVessel(Vessel vessel) {
        this.vessel = vesselOrNull(vessel);
        if (this.vessel != null) {
            this.setVesselID(this.vessel.getId());
        }
    }

    @ForeignKey(fromFieldName = "locationID", foreignFieldName = "id", joinType = Join.JoinType.LEFT_OUTER_JOIN)
    @Transient
    private LocationTO location;

    @Transient
    private DCSATransportType modeOfTransport;

    @JsonIgnore
    @ForeignKey(fromFieldName = "modeOfTransportID", foreignFieldName = "id", joinType = Join.JoinType.LEFT_OUTER_JOIN)
    @Transient
    private ModeOfTransport modeOfTransportEntity;

    // For loading the modeOfTransportEntity foreign key.
    public void setModeOfTransportEntity(ModeOfTransport modeOfTransportEntity) {
        if (modeOfTransportEntity != null) {
            this.modeOfTransport = modeOfTransportEntity.getDcsaTransportType();
            this.setModeOfTransportID(modeOfTransportEntity.getId());
        }
        this.modeOfTransportEntity = modeOfTransportEntity;
    }

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
            facilityCode = null;
            facilityCodeListProvider = null;
            UNLocationCode = null;
        }
        this.facility = facility;
    }

    @JsonIgnore
    @Transient
    @ForeignKey(fromFieldName = "importVoyageID", foreignFieldName = "id", viaJoinAlias = "im_voyage", joinType = Join.JoinType.LEFT_OUTER_JOIN)
    private Voyage importVoyage;

    @JsonIgnore
    @Transient
    @ForeignKey(fromFieldName = "exportVoyageID", foreignFieldName = "id", viaJoinAlias = "ex_voyage", joinType = Join.JoinType.LEFT_OUTER_JOIN)
    private Voyage exportVoyage;

    @Deprecated
    public String getCarrierVoyageNumber() {
        if (exportVoyage == null) {
            return null;
        }
        return getExportVoyageNumber();
    }

    public String getCarrierServiceCode() {
        if (exportVoyage == null) {
            return null;
        }
        Service service = exportVoyage.getService();
        if (service == null) {
            return null;
        }
        return service.getCarrierServiceCode();
    }

    public String getExportVoyageNumber() {
        if (exportVoyage == null) {
            return null;
        }
        return exportVoyage.getCarrierVoyageNumber();
    }

    public String getImportVoyageNumber() {
        if (importVoyage == null) {
            return null;
        }
        return importVoyage.getCarrierVoyageNumber();
    }

    public void setExportVoyageNumber(String voyageNumber) {
        if (exportVoyage == null) {
            exportVoyage = new Voyage();
        }
        exportVoyage.setCarrierVoyageNumber(voyageNumber);
    }

    public void setImportVoyageNumber(String voyageNumber) {
        if (importVoyage == null) {
            importVoyage = new Voyage();
        }
        importVoyage.setCarrierVoyageNumber(voyageNumber);
    }

    public void setCarrierServiceCode(String carrierServiceCode) {
        if (importVoyage == null) {
            importVoyage = new Voyage();
        }
        if (importVoyage.getService() == null) {
            importVoyage.setService(new Service());
        }
        if (exportVoyage == null) {
            exportVoyage = new Voyage();
        }
        if (exportVoyage.getService() == null) {
            exportVoyage.setService(new Service());
        }
        importVoyage.getService().setCarrierServiceCode(carrierServiceCode);
        exportVoyage.getService().setCarrierServiceCode(carrierServiceCode);
    }
}
