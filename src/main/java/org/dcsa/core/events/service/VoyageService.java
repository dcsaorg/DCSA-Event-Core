package org.dcsa.core.events.service;

import org.dcsa.core.events.model.Voyage;
import org.dcsa.core.service.ExtendedBaseService;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface VoyageService extends ExtendedBaseService<Voyage, UUID> {
    Mono<Voyage> findByTransportCallID(String transportCallID);
    Mono<Voyage> findByCarrierVoyageNumber(String carrierVoyageNumber);
}
