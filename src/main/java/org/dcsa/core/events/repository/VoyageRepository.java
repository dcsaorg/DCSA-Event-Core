package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Voyage;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Repository
public interface VoyageRepository extends ExtendedRepository<Voyage, UUID> {

  @Query(
      "  SELECT v.* FROM voyage v "
          + "JOIN transport_call_voyage tcv "
          + "ON v.id = tcv.voyage_id "
          + "WHERE tcv.transport_call_id = :transportCallID")
  Mono<Voyage> findByTransportCallID(String transportCallID);
}
