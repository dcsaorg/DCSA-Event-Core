package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.CargoItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface CargoItemRepository extends ReactiveCrudRepository<CargoItem, UUID>, CargoItemCustomRepository {

    Flux<CargoItem> findAllByShippingInstructionReference(String shippingInstructionReference);
    Mono<Void> deleteAllByIdIn(List<UUID> cargoItemIDs);

}
