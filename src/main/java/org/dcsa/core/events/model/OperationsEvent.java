package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.enums.*;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.dcsa.core.validator.EnumSubset;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("operations_event")
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("OPERATIONS")
public class OperationsEvent extends Event implements TransportCallBasedEvent {

    public static final String UNKNOWN_TIMESTAMP = "<Unknown timestamp>";

    @Column("transport_call_id")
    private String transportCallID;

    @Column("operations_event_type_code")
    private OperationsEventTypeCode operationsEventTypeCode;

    @Column("publisher_role")
    private PublisherRole publisherRole;

    @JsonIgnore
    @Column("event_location")
    private String eventLocationID;

    @Column("port_call_service_type_code")
    private PortCallServiceTypeCode portCallServiceTypeCode;

    @Column("facility_type_code")
    @EnumSubset(anyOf = {"PBPL", "BRTH"})
    private FacilityTypeCode facilityTypeCode;

    @Column("remark")
    private String remark;

    @Column("delay_reason_code")
    private String delayReasonCode;

    @JsonIgnore
    @Column("vessel_position")
    private String vesselPositionID;

    @Transient
    private TransportCallTO transportCall;

    @Transient
    private LocationTO eventLocation;

    @Transient
    private LocationTO vesselPosition;

    @JsonIgnore
    @Column("publisher")
    private String publisherID;

    @Transient
    private PartyTO publisher;

    /**
     * @return A name like "ETA-Berth" or "RTC Cargo Ops".  Unsupported variants will cause "Unknown" to be
     *  included in the return value.
     */
    @JsonIgnore
    @Transient
    public String getTimestampTypeName() {
        OperationsEventTypeCode eventTypeCode = getOperationsEventTypeCode();
        EventClassifierCode classifierCode = getEventClassifierCode();
        PortCallServiceTypeCode portCallServiceTypeCode = getPortCallServiceTypeCode();
        FacilityTypeCode facilityTypeCode = getFacilityTypeCode();
        if (eventTypeCode == null || classifierCode == null) {
            return UNKNOWN_TIMESTAMP;
        }
        // Special-cases
        if (facilityTypeCode == null && portCallServiceTypeCode == null) {
            // Special-case "Start/End of Seaway Passage"
            if (classifierCode != EventClassifierCode.ACT) {
                return UNKNOWN_TIMESTAMP;
            }
            switch (operationsEventTypeCode) {
                case ARRI:
                    return "EOSP";
                case DEPA:
                    return "SOSP";
                default:
                    return UNKNOWN_TIMESTAMP;
            }
        }
        if (portCallServiceTypeCode != null) {
            // Special-cases around FAST, Gangway and SAFE
            switch (portCallServiceTypeCode) {
                case FAST:
                    if (classifierCode != EventClassifierCode.ACT || facilityTypeCode != FacilityTypeCode.BRTH
                            || operationsEventTypeCode != OperationsEventTypeCode.ARRI) {
                        return UNKNOWN_TIMESTAMP;
                    }
                    return "AT All fast";
                case GWAY:
                    if (classifierCode != EventClassifierCode.ACT || facilityTypeCode != FacilityTypeCode.BRTH
                            || operationsEventTypeCode != OperationsEventTypeCode.ARRI) {
                        return UNKNOWN_TIMESTAMP;
                    }
                    return "Gangway Down and Safe";
                case SAFE:
                    if (classifierCode != EventClassifierCode.ACT || facilityTypeCode != FacilityTypeCode.BRTH) {
                        return UNKNOWN_TIMESTAMP;
                    }
                    switch (operationsEventTypeCode) {
                        case ARRI:
                            return "Vessel Readiness for cargo operations";
                        case DEPA:
                            return "Terminal ready for vessel departure";
                        default:
                            return UNKNOWN_TIMESTAMP;
                    }
            }
        }

        // Generic cases that follows the pattern "<X>T<Y>[ -]<Z>"  such as "ETA Berth" or "ETC-Cargo Ops"
        String suffix = null;

        if (portCallServiceTypeCode != null) {
            if (facilityTypeCode != portCallServiceTypeCode.getExpectedFacilityTypeCode()) {
                return UNKNOWN_TIMESTAMP;
            }
            suffix = portCallServiceTypeCode.getTimestampTypeNameSuffix();
        } else {
            // Due to previous cases, facilityTypeCode is guaranteed to be non-null here.  The assert is here to
            // catch if that changes in the future.
            assert facilityTypeCode != null;
            switch (facilityTypeCode) {
                case BRTH:
                    suffix = "Berth";
                    break;
                case PBPL:
                    suffix = "PBP";
                    break;
            }
        }
        if (suffix == null) {
            return UNKNOWN_TIMESTAMP;
        }
        return String.valueOf(classifierCode.name().charAt(0)) + 'T' + eventTypeCode.name().charAt(0) +
                ' ' + suffix;
    }

    @JsonIgnore
    public String getTransportCallID() {
        return transportCallID;
    }
}
