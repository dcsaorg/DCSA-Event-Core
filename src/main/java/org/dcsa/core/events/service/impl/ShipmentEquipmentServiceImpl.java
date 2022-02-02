package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.mapper.*;
import org.dcsa.core.events.model.transferobjects.*;
import org.dcsa.core.events.repository.*;
import org.dcsa.core.events.service.ReferenceService;
import org.dcsa.core.events.service.ShipmentEquipmentService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentEquipmentServiceImpl implements ShipmentEquipmentService {

  private final ShipmentEquipmentRepository shipmentEquipmentRepository;
  private final EquipmentRepository equipmentRepository;
  private final SealRepository sealRepository;
  private final ActiveReeferSettingsRepository activeReeferSettingsRepository;
  private final CargoItemRepository cargoItemRepository;
  private final CargoLineItemRepository cargoLineItemRepository;
  private final ReferenceService referenceService;

  private final SealMapper sealMapper;
  private final CargoLineItemMapper cargoLineItemMapper;
  private final CargoItemMapper cargoItemMapper;
  private final ActiveReeferSettingsMapper activeReeferSettingsMapper;
  private final EquipmentMapper equipmentMapper;
  private final ShipmentEquipmentMapper shipmentEquipmentMapper;

  @Override
  public Mono<List<ShipmentEquipmentTO>> createShipmentEquipment(
      UUID shipmentID, String shippingInstructionID, List<ShipmentEquipmentTO> shipmentEquipments) {
    if (Objects.isNull(shipmentEquipments) || shipmentEquipments.isEmpty()) {
      return Mono.just(Collections.emptyList());
    }

    return saveShipmentEquipment(shipmentID, shipmentEquipments)
        .flatMap(
            shipmentEquipmentIdShipmentEquipmentTO -> {
              UUID shipmentEquipmentID = shipmentEquipmentIdShipmentEquipmentTO.getT1();
              ShipmentEquipmentTO shipmentEquipmentTO =
                  shipmentEquipmentIdShipmentEquipmentTO.getT2();
              return Mono.when(
                      saveEquipment(shipmentEquipmentTO.getEquipment())
                          .doOnNext(shipmentEquipmentTO::setEquipment),
                      saveActiveReeferSettings(
                              shipmentEquipmentID, shipmentEquipmentTO.getActiveReeferSettings())
                          .doOnNext(shipmentEquipmentTO::setActiveReeferSettings),
                      saveSeals(shipmentEquipmentID, shipmentEquipmentTO.getSeals())
                          .doOnNext(shipmentEquipmentTO::setSeals),
                      saveCargoItems(
                              shipmentEquipmentID,
                              shippingInstructionID,
                              shipmentEquipmentTO.getCargoItems())
                          .doOnNext(shipmentEquipmentTO::setCargoItems))
                  .thenReturn(shipmentEquipmentTO);
            })
        .collectList();
  }

  // Returns Flux of Tuples of shipmentEquipmentID and ShipmentEquipmentTO)
  private Flux<Tuple2<UUID, ShipmentEquipmentTO>> saveShipmentEquipment(
      UUID shipmentID, List<ShipmentEquipmentTO> shipmentEquipmentList) {

    return Flux.fromIterable(shipmentEquipmentList)
        .flatMap(
            shipmentEquipmentTO ->
                shipmentEquipmentRepository
                    .save(shipmentEquipmentMapper.dtoToShipmentEquipment(shipmentEquipmentTO, shipmentID))
                    .flatMapMany(
                        shipmentEquipment ->
                            Mono.zip(
                                Mono.just(shipmentEquipment.getId()),
                                Mono.just(shipmentEquipmentTO))));
  }

  private Mono<EquipmentTO> saveEquipment(EquipmentTO equipmentTO) {
    return Mono.justOrEmpty(equipmentTO)
        .map(equipmentMapper::dtoToEquipment)
        .flatMap(equipmentRepository::save)
        .map(equipmentMapper::equipmentToDTO);
  }

  private Mono<ActiveReeferSettingsTO> saveActiveReeferSettings(
      UUID shipmentEquipmentID, ActiveReeferSettingsTO activeReeferSettingsTO) {
    return Mono.justOrEmpty(activeReeferSettingsTO)
        .map(arsTO -> activeReeferSettingsMapper.dtoToActiveReeferSettings(arsTO, shipmentEquipmentID))
        .flatMap(activeReeferSettingsRepository::save)
        .map(activeReeferSettingsMapper::activeReeferSettingsToDTO);
  }

  private Mono<List<SealTO>> saveSeals(UUID shipmentEquipmentID, List<SealTO> sealTOs) {
    if (Objects.isNull(sealTOs) || sealTOs.isEmpty()) {
      return Mono.just(Collections.emptyList());
    }
    return Flux.fromIterable(sealTOs)
        .map(sealTO -> sealMapper.dtoToSeal(sealTO, shipmentEquipmentID))
        .flatMap(sealRepository::save)
        .map(sealMapper::sealToDTO)
        .collectList();
  }

  private Mono<List<CargoItemTO>> saveCargoItems(
      UUID shipmentEquipmentID, String shippingInstructionID, List<CargoItemTO> cargoItemTOs) {
    if (Objects.isNull(cargoItemTOs) || cargoItemTOs.isEmpty()) {
      return Mono.just(Collections.emptyList());
    }
    return Flux.fromIterable(cargoItemTOs)
        .flatMap(
            cargoItemTO ->
                cargoItemRepository
                    .save(cargoItemMapper.dtoToCargoItem(cargoItemTO, shipmentEquipmentID, shippingInstructionID))
                    .map(cargoItem -> cargoItem.getId())
                    .zipWith(Mono.just(cargoItemTO))
                    .flatMap(t -> saveCargoLineItems(t.getT1(), cargoItemTO)))
        .flatMap(
            cargoItemTO ->
                referenceService
                    .createReferencesByShippingInstructionIDAndTOs(
                        shippingInstructionID, cargoItemTO.getReferences())
                    .thenReturn(cargoItemTO))
        .collectList();
  }

  private Mono<CargoItemTO> saveCargoLineItems(UUID cargoItemID, CargoItemTO cargoItemTO) {
    if (Objects.isNull(cargoItemTO.getCargoLineItems())
        || cargoItemTO.getCargoLineItems().isEmpty()) {
      return Mono.empty();
    }
    return Flux.fromIterable(cargoItemTO.getCargoLineItems())
      .map(cargoLineItemTO -> cargoLineItemMapper.dtoToCargoLineItem(cargoLineItemTO,cargoItemID))
        .flatMap(cargoLineItemRepository::save)
        .collectList()
        .map(
            cargoLineItems -> {
              cargoItemTO.setCargoLineItems(
                  cargoLineItems.stream()
                      .map(cargoLineItemMapper::cargoLineItemToDTO)
                      .collect(Collectors.toList()));
              return cargoItemTO;
            });
  }
}
