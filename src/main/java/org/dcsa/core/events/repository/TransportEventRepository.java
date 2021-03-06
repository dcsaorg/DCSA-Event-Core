package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.TransportEvent;
import org.dcsa.core.events.model.enums.EventClassifierCode;
import org.dcsa.core.events.model.enums.TransportEventTypeCode;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TransportEventRepository extends ExtendedRepository<TransportEvent, UUID> {

  // Explicit query because the database entity is missing elements from the model
  @Query(
      "SELECT transport_event.* "
          + "FROM transport_event "
          + "WHERE transport_event.event_classifier_code = :eventClassifierCode "
          + "AND transport_event.transport_event_type_code = :transportEventTypeCode "
          + "AND transport_event.transport_call_id = :transportCallId "
          + "ORDER BY transport_event.event_created_date_time "
          + "DESC LIMIT 1")
  Mono<TransportEvent>
      findFirstByTransportCallIDAndEventTypeCodeAndEventClassifierCodeOrderByEventDateTimeDesc(
          UUID transportCallId,
          TransportEventTypeCode transportEventTypeCode,
          EventClassifierCode eventClassifierCode);
}
