package org.dcsa.core.events.service;

import org.dcsa.core.events.model.Service;
import org.dcsa.core.service.ExtendedBaseService;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ServiceService extends ExtendedBaseService<Service, UUID>  {
    Mono<Service> findByCarrierServiceCode(String carrierServiceCode);
}
