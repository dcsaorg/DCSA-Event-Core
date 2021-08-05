package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.ModeOfTransport;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ModeOfTransportRepository extends ExtendedRepository<ModeOfTransport, String> {

  @Query(
      "SELECT mot.* from transport t " +
              "JOIN mode_of_transport mot " +
              "ON t.mode_of_transport = mot.mode_of_transport_code " +
              "WHERE t.discharge_transport_call_id = :transportCallID or t.load_transport_call_id = :transportCallID")
  Mono<ModeOfTransport> findByTransportCallID(String transportCallID);
}
