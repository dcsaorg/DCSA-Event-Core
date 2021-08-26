package org.dcsa.core.events.service;

import org.dcsa.core.events.model.TransportCallVoyage;
import org.dcsa.core.service.BaseService;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TransportCallVoyageService extends BaseService<TransportCallVoyage, String> {
    Mono<TransportCallVoyage> findByTransportCallIDAndVoyageID(String transportCallID, UUID voyageID);
}
