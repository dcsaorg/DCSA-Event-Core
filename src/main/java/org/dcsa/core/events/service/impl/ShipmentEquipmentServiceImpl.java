package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.CargoItem;
import org.dcsa.core.events.model.mapper.*;
import org.dcsa.core.events.model.transferobjects.*;
import org.dcsa.core.events.repository.*;
import org.dcsa.core.events.service.ReferenceService;
import org.dcsa.core.events.service.ShipmentEquipmentService;
import org.dcsa.core.exception.ConcreteRequestErrorMessageException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.*;
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

  private final ShipmentRepository shipmentRepository;

  private final SealMapper sealMapper;
  private final CargoLineItemMapper cargoLineItemMapper;
  private final CargoItemMapper cargoItemMapper;
  private final ActiveReeferSettingsMapper activeReeferSettingsMapper;
  private final EquipmentMapper equipmentMapper;
  private final ShipmentEquipmentMapper shipmentEquipmentMapper;

  @Override
  public Mono<List<ShipmentEquipmentTO>> findShipmentEquipmentByShipmentID(UUID shipmentID) {
    return shipmentEquipmentRepository
        .findShipmentEquipmentDetailsByShipmentID(shipmentID)
        .flatMap(
            shipmentEquipmentDetails -> {
              UUID shipmentEquipmentID = shipmentEquipmentDetails.getShipmentEquipmentID();
              ShipmentEquipmentTO shipmentEquipmentTO = new ShipmentEquipmentTO();
              shipmentEquipmentTO.setCarrierBookingReference(
                  shipmentEquipmentDetails.getCarrierBookingReference());
              shipmentEquipmentTO.setEquipment(
                  equipmentMapper.shipmentEquipmentDetailsToDTO(shipmentEquipmentDetails));
              shipmentEquipmentTO.setCargoGrossWeightUnit(
                  shipmentEquipmentDetails.getCargoGrossWeightUnit());
              shipmentEquipmentTO.setCargoGrossWeight(
                  shipmentEquipmentDetails.getCargoGrossWeight());
              shipmentEquipmentTO.setIsShipperOwned(shipmentEquipmentDetails.getIsShipperOwned());
              return Mono.when(
                      cargoItemRepository
                          .findAllCargoItemsAndCargoLineItemsByShipmentEquipmentID(
                              shipmentEquipmentID)
                          .flatMap(
                              cargoItemWithCargoLineItems ->
                                  referenceService
                                      .findByCargoItemID(cargoItemWithCargoLineItems.getId())
                                      .filter(
                                          refs ->
                                              (null != refs
                                                  && !refs
                                                      .isEmpty())) // mapNotNull was causing issues
                                      // with the chain, random
                                      // termination hence used a filter
                                      .map(
                                          referenceTOS -> {
                                            CargoItemTO cargoItemTO =
                                                cargoItemMapper.cargoItemWithCargoLineItemsToDTO(
                                                    cargoItemWithCargoLineItems);
                                            cargoItemTO.setReferences(referenceTOS);
                                            return cargoItemTO;
                                          })
                                      .switchIfEmpty(
                                          Mono.just(
                                              cargoItemMapper.cargoItemWithCargoLineItemsToDTO(
                                                  cargoItemWithCargoLineItems))))
                          .collectList()
                          .doOnNext(shipmentEquipmentTO::setCargoItems),
                      activeReeferSettingsRepository
                          .findById(shipmentEquipmentID)
                          .map(activeReeferSettingsMapper::activeReeferSettingsToDTO)
                          .doOnNext(shipmentEquipmentTO::setActiveReeferSettings),
                      sealRepository
                          .findAllByShipmentEquipmentID(shipmentEquipmentID)
                          .map(sealMapper::sealToDTO)
                          .collectList()
                          .doOnNext(shipmentEquipmentTO::setSeals))
                  .thenReturn(shipmentEquipmentTO);
            })
        .collectList();
  }

  @Override
  public Mono<List<ShipmentEquipmentTO>> resolveShipmentEquipmentsForShippingInstructionReference(
      List<ShipmentEquipmentTO> shipmentEquipments, ShippingInstructionTO shippingInstructionTO) {
    return cargoItemRepository
        .findAllByShippingInstructionReference(
            shippingInstructionTO.getShippingInstructionReference())
        .flatMap(
            cargoItems ->
                Mono.when(
                        sealRepository.deleteAllByShipmentEquipmentID(
                            cargoItems.getShipmentEquipmentID()),
                        activeReeferSettingsRepository.deleteByShipmentEquipmentID(
                            cargoItems.getShipmentEquipmentID()))
                    .thenReturn(cargoItems))
        .flatMap(
            cargoItem ->
                shipmentEquipmentRepository
                    .findShipmentEquipmentByShipmentID(cargoItem.getShipmentEquipmentID())
                    .flatMap(
                        shipmentEquipment ->
                            equipmentRepository
                                .deleteAllByEquipmentReference(
                                    shipmentEquipment.getEquipmentReference())
                                .thenReturn(shipmentEquipment))
                    .flatMap(
                        shipmentEquipment ->
                            shipmentEquipmentRepository
                                .deleteShipmentEquipmentByShipmentID(
                                    shipmentEquipment.getShipmentID())
                                .thenReturn(new ShipmentEquipmentTO()))
                    .thenReturn(cargoItem))
        .flatMap(
            cargoItem ->
                cargoLineItemRepository
                    .deleteByCargoItemID(cargoItem.getId())
                    .thenReturn(cargoItem))
        .flatMap(
            cargoItem -> cargoItemRepository.deleteById(cargoItem.getId()).thenReturn(cargoItem))
        .collectList()
        .flatMap(
            ignored ->
                addShipmentEquipmentToShippingInstruction(
                    shipmentEquipments, shippingInstructionTO));
  }

  @Override
  public Mono<List<ShipmentEquipmentTO>> createShipmentEquipment(
      UUID shipmentID,
      String shippingInstructionReference,
      List<ShipmentEquipmentTO> shipmentEquipments) {
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
                              shippingInstructionReference,
                              shipmentEquipmentTO.getCargoItems())
                          .doOnNext(shipmentEquipmentTO::setCargoItems))
                  .thenReturn(shipmentEquipmentTO);
            })
        .collectList();
  }

  @Override
  public Mono<List<ShipmentEquipmentTO>> addShipmentEquipmentToShippingInstruction(
      List<ShipmentEquipmentTO> shipmentEquipments, ShippingInstructionTO shippingInstructionTO) {
    if (shipmentEquipments == null) return Mono.empty();
    List<String> carrierBookingReferences = getCarrierBookingReferences(shippingInstructionTO);

    return Flux.fromIterable(carrierBookingReferences)
        .flatMap(
            carrierBookingReference ->
                shipmentRepository
                    .findByCarrierBookingReference(carrierBookingReference)
                    .switchIfEmpty(
                        Mono.error(
                            ConcreteRequestErrorMessageException.invalidParameter(
                                "No shipment found with carrierBookingReference: "
                                    + carrierBookingReference))))
        .flatMap(
            shipment ->
                createShipmentEquipment(
                    shipment.getShipmentID(),
                    shippingInstructionTO.getShippingInstructionReference(),
                    shipmentEquipments))
        .flatMapIterable(shipmentEquipmentTOS -> shipmentEquipmentTOS)
        .collectList();
  }

  List<String> getCarrierBookingReferences(ShippingInstructionTO shippingInstructionTO) {
    if (shippingInstructionTO.getCarrierBookingReference() == null) {
      List<String> carrierBookingReferences = new ArrayList<>();
      for (ShipmentEquipmentTO shipmentEquipmentTO :
          shippingInstructionTO.getShipmentEquipments()) {
        carrierBookingReferences.add(shipmentEquipmentTO.getCarrierBookingReference());
      }
      carrierBookingReferences.removeAll(Collections.singleton(null));
      return carrierBookingReferences;
    }
    return List.of(shippingInstructionTO.getCarrierBookingReference());
  }

  // Returns Flux of Tuples of shipmentEquipmentID and ShipmentEquipmentTO)
  private Flux<Tuple2<UUID, ShipmentEquipmentTO>> saveShipmentEquipment(
      UUID shipmentID, List<ShipmentEquipmentTO> shipmentEquipmentList) {

    return Flux.fromIterable(shipmentEquipmentList)
        .flatMap(
            shipmentEquipmentTO ->
                shipmentEquipmentRepository
                    .save(
                        shipmentEquipmentMapper.dtoToShipmentEquipment(
                            shipmentEquipmentTO, shipmentID))
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
        .map(
            arsTO ->
                activeReeferSettingsMapper.dtoToActiveReeferSettings(
                    arsTO, shipmentEquipmentID, true))
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
      UUID shipmentEquipmentID,
      String shippingInstructionReference,
      List<CargoItemTO> cargoItemTOs) {
    if (Objects.isNull(cargoItemTOs) || cargoItemTOs.isEmpty()) {
      return Mono.just(Collections.emptyList());
    }
    return Flux.fromIterable(cargoItemTOs)
        .flatMap(
            cargoItemTO ->
                cargoItemRepository
                    .save(
                        cargoItemMapper.dtoToCargoItem(
                            cargoItemTO, shipmentEquipmentID, shippingInstructionReference))
                    .map(CargoItem::getId)
                    .zipWith(Mono.just(cargoItemTO))
                    .flatMap(t -> saveCargoLineItems(t.getT1(), cargoItemTO)))
        .flatMap(
            cargoItemTO ->
                referenceService
                    .createReferencesByShippingInstructionReferenceAndTOs(
                        shippingInstructionReference, cargoItemTO.getReferences())
                    .thenReturn(cargoItemTO))
        .collectList();
  }

  private Mono<CargoItemTO> saveCargoLineItems(UUID cargoItemID, CargoItemTO cargoItemTO) {
    if (Objects.isNull(cargoItemTO.getCargoLineItems())
        || cargoItemTO.getCargoLineItems().isEmpty()) {
      return Mono.empty();
    }
    return Flux.fromIterable(cargoItemTO.getCargoLineItems())
        .map(
            cargoLineItemTO -> cargoLineItemMapper.dtoToCargoLineItem(cargoLineItemTO, cargoItemID))
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
