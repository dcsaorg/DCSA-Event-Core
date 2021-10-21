package org.dcsa.core.events.model;

import lombok.Data;
import org.dcsa.core.events.model.enums.*;
import org.dcsa.core.validator.EnumSubset;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("timestamp_definition")
@Data
public class TimestampDefinition {

    @Id
    @Column("id")
    private String id;

    @Column("timestamp_type_name")
    private String timestampTypeName;

    @Column("publisher_role")
    private PublisherRole publisherRole;

    @Column("primary_receiver")
    private PublisherRole primaryReceiver;

    @Column("event_classifier_code")
    private EventClassifierCode eventClassifierCode;

    @Column("operations_event_type_code")
    private OperationsEventTypeCode operationsEventTypeCode;

    // TODO: Create a PortCallPhaseTypeCode class
    @Column("port_call_phase_type_code")
    private String portCallPhaseTypeCode;

    @Column("port_call_service_type_code")
    private PortCallServiceTypeCode portCallServiceTypeCode;

    @Column("facility_type_code")
    @EnumSubset(anyOf = {"PBPL", "BRTH"})
    private FacilityTypeCode facilityTypeCode;

    @Column("is_berth_location_needed")
    private Boolean isBerthLocationNeeded;

    @Column("is_pbp_location_needed")
    private Boolean isPBPLocationNeeded;

    @Column("is_terminal_needed")
    private Boolean isTerminalNeeded;

    @Column("is_vessel_position_needed")
    private Boolean isVesselPositionNeeded;

    @Column("negotiation_cycle")
    private String negotiationCycle;

    @Column("provided_in_standard")
    private String providedInStandard;
}
