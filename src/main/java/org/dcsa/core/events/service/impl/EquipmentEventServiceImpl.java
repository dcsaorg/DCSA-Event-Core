package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.EquipmentEvent;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.core.events.repository.EquipmentEventRepository;
import org.dcsa.core.events.service.EquipmentEventService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EquipmentEventServiceImpl extends ExtendedBaseServiceImpl<EquipmentEventRepository, EquipmentEvent, UUID> implements EquipmentEventService {
    private final EquipmentEventRepository equipmentEventRepository;

    @Override
    public EquipmentEventRepository getRepository() {
        return equipmentEventRepository;
    }

    //Overriding base method here, as it marks empty results as an error, meaning we can't use switchOnEmpty()
    @Override
    public Mono<EquipmentEvent> findById(UUID id) {
        return getRepository().findById(id);
    }
}
