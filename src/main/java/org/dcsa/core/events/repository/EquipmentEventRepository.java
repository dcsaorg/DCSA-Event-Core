package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.EquipmentEvent;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface EquipmentEventRepository extends ExtendedRepository<EquipmentEvent, UUID> {

    @Query("SELECT * FROM equipment_event a WHERE (:eventType IS NULL or a.event_type =:eventType) AND (:equipmentReference IS NULL or a.equipment_reference =:equipmentReference) ")
    Flux<EquipmentEvent> findAllEquipmentEventsByFilters(@Param("eventType") EventType eventType, String bookingReference, @Param("equipmentReference") String equipmentReference);
}
