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

    @JsonIgnore
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
        if (eventTypeCode == null || classifierCode == null) {
            return "<Unknown timestamp>";
        }
        StringBuilder timestampType = new StringBuilder(25);
        char separator = '-';
        // Create the "ETA_" (etc.) prefix
        timestampType.append(classifierCode.name().charAt(0)).append('T').append(eventTypeCode.name().charAt(0));
        String suffix = "(Unknown)";

        if (getPortCallServiceTypeCode() != null) {
            // For some reason, Cargo Ops and Pilot timestamps uses ' ' instead of '-' as separator. Mirror that
            separator = ' ';
            // Strictly speaking, we should ensure that facilityTypeCode is BRTH for these, but it is probably
            // not worth it
            switch (getPortCallServiceTypeCode()) {
                case CRGO:
                    suffix = "Cargo Ops";
                    break;
                case PILO:
                    suffix = "Pilot";
                    break;
            }
        } else if (getFacilityTypeCode() != null) {
            switch (getFacilityTypeCode()) {
                case BRTH:
                    suffix = "Berth";
                    break;
                case PBPL:
                    suffix = "PBP";
                    break;
            }
        }
        return timestampType.append(separator).append(suffix).toString();
    }
}
