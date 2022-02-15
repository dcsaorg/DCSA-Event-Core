package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.ModeOfTransport;
import org.dcsa.core.events.model.enums.DCSATransportType;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ModeOfTransportRepository extends ReactiveCrudRepository<ModeOfTransport, String> {

  @Query(
      "SELECT mot.* from transport_call tc " +
              "JOIN mode_of_transport mot " +
              "ON tc.mode_of_transport_code = mot.mode_of_transport_code " +
              "WHERE tc.id = :transportCallID")
  Mono<ModeOfTransport> findByTransportCallID(String transportCallID);

  Mono<ModeOfTransport> findByDcsaTransportType(DCSATransportType dcsaTransportType);
}
