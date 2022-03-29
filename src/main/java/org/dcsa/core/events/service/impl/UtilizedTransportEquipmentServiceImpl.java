package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.CargoItem;
import org.dcsa.core.events.model.mapper.*;
import org.dcsa.core.events.model.transferobjects.*;
import org.dcsa.core.events.repository.*;
import org.dcsa.core.events.service.ReferenceService;
import org.dcsa.core.events.service.UtilizedTransportEquipmentService;
import org.dcsa.core.exception.ConcreteRequestErrorMessageException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UtilizedTransportEquipmentServiceImpl implements UtilizedTransportEquipmentService {

  private final UtilizedTransportEquipmentRepository utilizedTransportEquipmentRepository;
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
  private final UtilizedTransportEquipmentMapper utilizedTransportEquipmentMapper;

  @Override
  public Mono<List<UtilizedTransportEquipmentTO>> findUtilizedTransportEquipmentByShipmentID(
      UUID shipmentID) {
    return utilizedTransportEquipmentRepository
        .findUtilizedTransportEquipmentDetailsByShipmentID(shipmentID)
        .flatMap(
            utilizedTransportEquipmentDetails -> {
              UUID utilizedTransportEquipmentID =
                  utilizedTransportEquipmentDetails.getUtilizedTransportEquipmentID();
              UtilizedTransportEquipmentTO utilizedTransportEquipmentTO =
                  new UtilizedTransportEquipmentTO();
              utilizedTransportEquipmentTO.setCarrierBookingReference(
                  utilizedTransportEquipmentDetails.getCarrierBookingReference());
              utilizedTransportEquipmentTO.setEquipment(
                  equipmentMapper.utilizedTransportEquipmentDetailsToDTO(
                      utilizedTransportEquipmentDetails));
              utilizedTransportEquipmentTO.setCargoGrossWeightUnit(
                  utilizedTransportEquipmentDetails.getCargoGrossWeightUnit());
              utilizedTransportEquipmentTO.setCargoGrossWeight(
                  utilizedTransportEquipmentDetails.getCargoGrossWeight());
              utilizedTransportEquipmentTO.setIsShipperOwned(
                  utilizedTransportEquipmentDetails.getIsShipperOwned());
              return Mono.when(
                      cargoItemRepository
                          .findAllCargoItemsAndCargoLineItemsByUtilizedTransportEquipmentID(
                              utilizedTransportEquipmentID)
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
                          .doOnNext(utilizedTransportEquipmentTO::setCargoItems),
                      activeReeferSettingsRepository
                          .findById(utilizedTransportEquipmentID)
                          .map(activeReeferSettingsMapper::activeReeferSettingsToDTO)
                          .doOnNext(utilizedTransportEquipmentTO::setActiveReeferSettings),
                      sealRepository
                          .findAllByUtilizedTransportEquipmentID(utilizedTransportEquipmentID)
                          .map(sealMapper::sealToDTO)
                          .collectList()
                          .doOnNext(utilizedTransportEquipmentTO::setSeals))
                  .thenReturn(utilizedTransportEquipmentTO);
            })
        .collectList();
  }

  @Override
  public Mono<List<UtilizedTransportEquipmentTO>>
      resolveUtilizedTransportEquipmentsForShippingInstructionReference(
          List<UtilizedTransportEquipmentTO> utilizedTransportEquipmentTOs,
          ShippingInstructionTO shippingInstructionTO) {
    return cargoItemRepository
        .findAllByShippingInstructionReference(
            shippingInstructionTO.getShippingInstructionReference())
        .flatMap(
            cargoItems ->
                Mono.when(
                        sealRepository.deleteAllByUtilizedTransportEquipmentID(
                            cargoItems.getUtilizedTransportEquipmentID()),
                        activeReeferSettingsRepository.deleteByUtilizedTransportEquipmentID(
                            cargoItems.getUtilizedTransportEquipmentID()))
                    .thenReturn(cargoItems))
        .flatMap(
            cargoItem ->
                utilizedTransportEquipmentRepository
                    .findUtilizedTransportEquipmentByShipmentID(
                        cargoItem.getUtilizedTransportEquipmentID())
                    .flatMap(
                        utilizedTransportEquipment ->
                            equipmentRepository
                                .deleteAllByEquipmentReference(
                                    utilizedTransportEquipment.getEquipmentReference())
                                .thenReturn(utilizedTransportEquipment))
                    .flatMap(
                        utilizedTransportEquipment ->
                            utilizedTransportEquipmentRepository
                                .deleteUtilizedTransportEquipmentByShipmentID(
                                    utilizedTransportEquipment.getShipmentID())
                                .thenReturn(new UtilizedTransportEquipmentTO()))
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
                addUtilizedTransportEquipmentToShippingInstruction(
                    utilizedTransportEquipmentTOs, shippingInstructionTO));
  }

  @Override
  public Mono<List<UtilizedTransportEquipmentTO>> createUtilizedTransportEquipment(
      UUID shipmentID,
      String shippingInstructionReference,
      List<UtilizedTransportEquipmentTO> utilizedTransportEquipmentTOs) {
    if (Objects.isNull(utilizedTransportEquipmentTOs) || utilizedTransportEquipmentTOs.isEmpty()) {
      return Mono.just(Collections.emptyList());
    }

    return saveUtilizedTransportEquipment(shipmentID, utilizedTransportEquipmentTOs)
        .flatMap(
            tuple -> {
              UUID utilizedTransportEquipmentID = tuple.getT1();
              UtilizedTransportEquipmentTO utilizedTransportEquipmentTO = tuple.getT2();
              return Mono.when(
                      saveEquipment(utilizedTransportEquipmentTO.getEquipment())
                          .doOnNext(utilizedTransportEquipmentTO::setEquipment),
                      saveActiveReeferSettings(
                              utilizedTransportEquipmentID,
                              utilizedTransportEquipmentTO.getActiveReeferSettings())
                          .doOnNext(utilizedTransportEquipmentTO::setActiveReeferSettings),
                      saveSeals(
                              utilizedTransportEquipmentID, utilizedTransportEquipmentTO.getSeals())
                          .doOnNext(utilizedTransportEquipmentTO::setSeals))
                  .thenReturn(utilizedTransportEquipmentTO);
            })
        .collectList();
  }

  @Override
  public Mono<List<UtilizedTransportEquipmentTO>>
      addUtilizedTransportEquipmentToShippingInstruction(
          List<UtilizedTransportEquipmentTO> utilizedTransportEquipmentTOs,
          ShippingInstructionTO shippingInstructionTO) {
    if (utilizedTransportEquipmentTOs == null) return Mono.empty();
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
                createUtilizedTransportEquipment(
                    shipment.getShipmentID(),
                    shippingInstructionTO.getShippingInstructionReference(),
                    utilizedTransportEquipmentTOs))
        .flatMapIterable(Function.identity())
        .collectList();
  }

  List<String> getCarrierBookingReferences(ShippingInstructionTO shippingInstructionTO) {
    if (shippingInstructionTO.getCarrierBookingReference() == null) {
      List<String> carrierBookingReferences = new ArrayList<>();
      for (UtilizedTransportEquipmentTO utilizedTransportEquipmentTO :
          shippingInstructionTO.getUtilizedTransportEquipments()) {
        carrierBookingReferences.add(utilizedTransportEquipmentTO.getCarrierBookingReference());
      }
      carrierBookingReferences.removeAll(Collections.singleton(null));
      return carrierBookingReferences;
    }
    return List.of(shippingInstructionTO.getCarrierBookingReference());
  }

  // Returns Flux of Tuples of utilizedTransportEquipmentID and UtilizedEquipmentTO
  private Flux<Tuple2<UUID, UtilizedTransportEquipmentTO>> saveUtilizedTransportEquipment(
      UUID shipmentID, List<UtilizedTransportEquipmentTO> utilizedTransportEquipmentTOList) {

    return Flux.fromIterable(utilizedTransportEquipmentTOList)
        .flatMap(
            utilizedTransportEquipmentTO ->
                utilizedTransportEquipmentRepository
                    .save(
                        utilizedTransportEquipmentMapper.dtoToUtilizedTransportEquipment(
                            utilizedTransportEquipmentTO, shipmentID))
                    .flatMapMany(
                        utilizedTransportEquipment ->
                            Mono.zip(
                                Mono.just(utilizedTransportEquipment.getId()),
                                Mono.just(utilizedTransportEquipmentTO))));
  }

  private Mono<EquipmentTO> saveEquipment(EquipmentTO equipmentTO) {
    return Mono.justOrEmpty(equipmentTO)
        .map(equipmentMapper::dtoToEquipment)
        .flatMap(equipmentRepository::save)
        .map(equipmentMapper::equipmentToDTO);
  }

  private Mono<ActiveReeferSettingsTO> saveActiveReeferSettings(
      UUID utilizedTransportEquipmentID, ActiveReeferSettingsTO activeReeferSettingsTO) {
    return Mono.justOrEmpty(activeReeferSettingsTO)
        .map(
            arsTO ->
                activeReeferSettingsMapper.dtoToActiveReeferSettings(
                    arsTO, utilizedTransportEquipmentID, true))
        .flatMap(activeReeferSettingsRepository::save)
        .map(activeReeferSettingsMapper::activeReeferSettingsToDTO);
  }

  private Mono<List<SealTO>> saveSeals(UUID utilizedTransportEquipmentID, List<SealTO> sealTOs) {
    if (Objects.isNull(sealTOs) || sealTOs.isEmpty()) {
      return Mono.just(Collections.emptyList());
    }
    return Flux.fromIterable(sealTOs)
        .map(sealTO -> sealMapper.dtoToSeal(sealTO, utilizedTransportEquipmentID))
        .flatMap(sealRepository::save)
        .map(sealMapper::sealToDTO)
        .collectList();
  }

  private Mono<List<CargoItemTO>> saveCargoItems(
      UUID utilizedTransportEquipmentID,
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
                            cargoItemTO,
                            utilizedTransportEquipmentID,
                            shippingInstructionReference))
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
