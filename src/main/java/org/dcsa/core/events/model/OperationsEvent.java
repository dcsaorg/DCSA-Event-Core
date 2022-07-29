package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.enums.EventClassifierCode;
import org.dcsa.core.events.model.enums.OperationsEventTypeCode;
import org.dcsa.core.events.model.enums.PortCallPhaseTypeCode;
import org.dcsa.core.events.model.enums.PortCallServiceTypeCode;
import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.dcsa.core.validator.EnumSubset;
import org.dcsa.skernel.model.enums.FacilityTypeCode;
import org.dcsa.skernel.model.enums.PartyFunction;
import org.dcsa.skernel.model.transferobjects.LocationTO;
import org.dcsa.skernel.model.transferobjects.PartyTO;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;
import java.util.UUID;

@Table("operations_event")
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("OPERATIONS")
public class OperationsEvent extends Event implements TransportCallBasedEvent {

    @Column("transport_call_id")
    private UUID transportCallID;

    @Column("operations_event_type_code")
    private OperationsEventTypeCode operationsEventTypeCode;

    @Column("publisher_role")
    @EnumSubset(anyOf = {"CA", "AG", "VSL", "ATH", "PLT", "TR", "TWG", "BUK", "LSH"})
    private PartyFunction publisherRole;

    @JsonIgnore
    @Column("event_location")
    private UUID eventLocationID;

    @Column("port_call_phase_type_code")
    private PortCallPhaseTypeCode portCallPhaseTypeCode;

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
    private UUID vesselPositionID;

    @Transient
    private TransportCallTO transportCall;

    @Transient
    private LocationTO eventLocation;

    @Transient
    private LocationTO vesselPosition;

    @JsonIgnore
    @Column("publisher")
    private UUID publisherID;

    @Transient
    private PartyTO publisher;

    @JsonIgnore
    public UUID getTransportCallID() {
        return transportCallID;
    }

    public void ensurePhaseTypeIsDefined() {
        if (portCallPhaseTypeCode != null) {
            return;
        }
        if (portCallServiceTypeCode != null) {
            Set<PortCallPhaseTypeCode> validPhases = portCallServiceTypeCode.getValidPhases();
            if (validPhases.size() == 1) {
                portCallPhaseTypeCode = validPhases.iterator().next();
            }
        } else if (facilityTypeCode != null) {
            switch (facilityTypeCode) {
                case BRTH:
                    if (operationsEventTypeCode == OperationsEventTypeCode.ARRI) {
                        if (getEventClassifierCode() == EventClassifierCode.ACT) {
                            portCallPhaseTypeCode = PortCallPhaseTypeCode.ALGS;
                        } else {
                            portCallPhaseTypeCode = PortCallPhaseTypeCode.INBD;
                        }
                    }
                    if (operationsEventTypeCode == OperationsEventTypeCode.DEPA) {
                        if (getEventClassifierCode() == EventClassifierCode.ACT) {
                            portCallPhaseTypeCode = PortCallPhaseTypeCode.OUTB;
                        } else {
                            portCallPhaseTypeCode = PortCallPhaseTypeCode.ALGS;
                        }
                    }
                    break;
                case PBPL:
                    portCallPhaseTypeCode = PortCallPhaseTypeCode.INBD;
                    break;
            }
        }
        if (portCallPhaseTypeCode == null) {
            throw new IllegalStateException("Ambiguous timestamp");
        }
    }
}
