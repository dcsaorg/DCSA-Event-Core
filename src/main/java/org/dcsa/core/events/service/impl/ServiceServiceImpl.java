package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Service;
import org.dcsa.core.events.repository.ServiceRepository;
import org.dcsa.core.events.service.ServiceService;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@org.springframework.stereotype.Service
public class ServiceServiceImpl extends ExtendedBaseServiceImpl<ServiceRepository, Service, UUID> implements ServiceService {

    private final ServiceRepository serviceRepository;

    @Override
    public ServiceRepository getRepository() {
        return serviceRepository;
    }

    @Override
    public Mono<Service> findByCarrierServiceCode(String carrierServiceCode) {
        return serviceRepository.findByCarrierServiceCode(carrierServiceCode);
    }

}
