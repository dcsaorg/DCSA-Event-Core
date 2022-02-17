package org.dcsa.core.events.service;

import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.dcsa.core.service.QueryService;
import reactor.core.publisher.Mono;

public interface AbstractTransportCallTOService<T extends TransportCallTO> extends QueryService<T, String> {

    Mono<T> findById(String id);

    Mono<T> create(T transportCallTO);
}
