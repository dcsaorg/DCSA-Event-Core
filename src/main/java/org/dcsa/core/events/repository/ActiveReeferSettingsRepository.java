package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.ActiveReeferSettings;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ActiveReeferSettingsRepository extends ReactiveCrudRepository<ActiveReeferSettings, UUID> {
}
