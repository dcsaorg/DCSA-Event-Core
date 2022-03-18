package org.dcsa.core.events.service;

import org.dcsa.core.events.model.Service;
import reactor.core.publisher.Mono;

public interface ServiceService {
    Mono<Service> findByCarrierServiceCode(String carrierServiceCode);

    Mono<Service> create(Service service);
}
