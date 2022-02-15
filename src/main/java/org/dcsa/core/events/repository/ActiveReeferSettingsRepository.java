package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.ActiveReeferSettings;
import org.dcsa.core.repository.ExtendedRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ActiveReeferSettingsRepository extends ExtendedRepository<ActiveReeferSettings, UUID> {
    Mono<Void> deleteByShipmentEquipmentID(UUID shipmentEquipmentID);
}
