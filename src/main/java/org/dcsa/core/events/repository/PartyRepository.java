package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Party;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.Objects;

public interface PartyRepository extends ReactiveCrudRepository<Party, String> {
  default Mono<Party> findByIdOrEmpty(String id) {
    if (Objects.isNull(id)) {
      return Mono.empty();
    } else {
      return findById(id);
    }
  }
}
