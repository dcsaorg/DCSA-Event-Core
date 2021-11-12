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
    @EnumSubset(anyOf = {"CA", "AG", "VSL", "ATH", "PLT", "TR", "TWG", "BUK", "LSH"})
    private PartyFunction publisherRole;

    @Column("primary_receiver")
    @EnumSubset(anyOf = {"CA", "AG", "VSL", "ATH", "PLT", "TR", "TWG", "BUK", "LSH"})
    private PartyFunction primaryReceiver;

    @Column("event_classifier_code")
    private EventClassifierCode eventClassifierCode;

    @Column("operations_event_type_code")
    private OperationsEventTypeCode operationsEventTypeCode;

    @Column("port_call_phase_type_code")
    private PortCallPhaseTypeCode portCallPhaseTypeCode;

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

    @Column("accept_timestamp_definition")
    private String acceptTimestampDefinition;

    @Column("reject_timestamp_definition")
    private String rejectTimestampDefinition;

    @Column("canonical_timestamp_definition")
    private String canonicalTimestampDefinition;
}
