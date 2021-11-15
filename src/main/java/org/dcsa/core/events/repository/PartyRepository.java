package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Party;
import org.dcsa.core.repository.ExtendedRepository;
import reactor.core.publisher.Mono;

import java.util.Objects;

public interface PartyRepository extends ExtendedRepository<Party, String> {
  default Mono<Party> findByIdOrEmpty(String id) {
    if (Objects.isNull(id)) {
      return Mono.empty();
    } else {
      return findById(id);
    }
  }
}
