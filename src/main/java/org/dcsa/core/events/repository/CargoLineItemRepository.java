package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.CargoLineItem;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface CargoLineItemRepository extends ReactiveCrudRepository<CargoLineItem, UUID> {

    Flux<CargoLineItem> findAllByCargoItemID(UUID cargoItemID);
    Mono<Void> deleteByCargoItemID(UUID cargoItemID);
    Mono<Void> deleteByCargoItemIDAndCargoLineItemIDIn(UUID cargoItemID, List<String> cargoLineItemIDs);
}
