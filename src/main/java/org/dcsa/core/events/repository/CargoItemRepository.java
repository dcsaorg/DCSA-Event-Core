package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.CargoItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface CargoItemRepository
    extends ReactiveCrudRepository<CargoItem, UUID>, CargoItemCustomRepository {

  @Query(
      "SELECT DISTINCT ci.* FROM shipping_instruction si "
          + "JOIN consignment_item con ON con.shipping_instruction_id = si.id "
          + "JOIN cargo_item ci ON ci.consignment_item_id = con.id "
          + "WHERE si.id = :shippingInstructionReference")
  Flux<CargoItem> findAllByShippingInstructionReference(String shippingInstructionReference);

  Mono<Void> deleteAllByIdIn(List<UUID> cargoItemIDs);
}
