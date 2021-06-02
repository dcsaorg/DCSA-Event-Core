package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.ShipmentEvent;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.core.events.repository.ShipmentEventRepository;
import org.dcsa.core.events.service.ShipmentEventService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;


@RequiredArgsConstructor
@Service
public class ShipmentEventServiceImpl extends ExtendedBaseServiceImpl<ShipmentEventRepository, ShipmentEvent, UUID> implements ShipmentEventService {
    private final ShipmentEventRepository shipmentEventRepository;

    @Override
    public ShipmentEventRepository getRepository() {
        return shipmentEventRepository;
    }

    //Overriding base method here, as it marks empty results as an error, meaning we can't use switchOnEmpty()
    @Override
    public Mono<ShipmentEvent> findById(UUID id) {
        return getRepository().findById(id);
    }
}
