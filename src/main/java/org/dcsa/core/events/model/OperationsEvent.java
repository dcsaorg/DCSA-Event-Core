package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.enums.FacilityTypeCode;
import org.dcsa.core.events.model.enums.OperationsEventTypeCode;
import org.dcsa.core.events.model.enums.PortCallServiceTypeCode;
import org.dcsa.core.events.model.enums.PublisherRole;
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
public class OperationsEvent extends Event {

    @JsonIgnore
    @Column("transport_call_id")
    private String transportCallID;

    @Column("operations_event_type_code")
    private OperationsEventTypeCode operationsEventTypeCode;

    @Column("publisher_role")
    private PublisherRole publisherRole;

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

    @Column("vessel_position")
    private String vesselPositionID;

    @Transient
    private TransportCallTO transportCall;

    @Transient
    private LocationTO eventLocation;

    @Transient
    private LocationTO vesselPosition;

    @Column("publisher")
    private String publisherID;

    @Transient
    private PartyTO publisher;
}
