package org.dcsa.core.events.edocumentation.service;

import org.dcsa.core.events.edocumentation.model.transferobject.TransportTO;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface TransportService {
    Flux<TransportTO> findByTransportID(UUID id);
}
