package org.dcsa.core.events.service;

import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.dcsa.core.service.QueryService;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AbstractTransportCallTOService<T extends TransportCallTO> extends QueryService<T, UUID> {

    Mono<T> findById(UUID id);

    Mono<T> create(T transportCallTO);
}
