package org.dcsa.core.events.repository;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.dcsa.core.events.model.CargoItem;
import org.dcsa.core.events.model.CargoLineItem;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

public interface CargoItemCustomRepository {

  Flux<CargoItemWithCargoLineItems> findAllCargoItemsAndCargoLineItemsByShipmentEquipmentID(
      UUID shipmentEquipmentID);

  @Data
  @ToString(callSuper = true)
  @EqualsAndHashCode(callSuper = true)
  class CargoItemWithCargoLineItems extends CargoItem {
    List<CargoLineItem> cargoLineItems;
  }
}
