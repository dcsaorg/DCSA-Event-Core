package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.TimestampDefinition;
import org.dcsa.core.events.model.enums.*;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TimestampDefinitionRepository extends ExtendedRepository<TimestampDefinition,String> {

    @Modifying
    @Query("INSERT INTO ops_event_timestamp_definition (event_id, timestamp_definition) VALUES (:eventID, :timestampDefinitionID)")
    Mono<Void> markOperationsEventAsTimestamp(UUID eventID, String timestampDefinitionID);

    Mono<TimestampDefinition> findByPublisherRoleAndEventClassifierCodeAndOperationsEventTypeCodeAndPortCallPhaseTypeCodeAndPortCallServiceTypeCodeAndFacilityTypeCode(
            PartyFunction publisherRole,
            EventClassifierCode eventClassifierCode,
            OperationsEventTypeCode operationsEventTypeCode,
            PortCallPhaseTypeCode portCallPhaseTypeCode,
            PortCallServiceTypeCode portCallServiceTypeCode,
            FacilityTypeCode facilityTypeCode
    );
}
