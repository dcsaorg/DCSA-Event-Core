package org.dcsa.core.events.edocumentation.service.impl;

import lombok.AllArgsConstructor;
import org.dcsa.core.events.edocumentation.model.mapper.ConsignmentItemMapper;
import org.dcsa.core.events.edocumentation.model.transferobject.ConsignmentItemTO;
import org.dcsa.core.events.edocumentation.repository.ConsignmentItemRepository;
import org.dcsa.core.events.edocumentation.service.ConsignmentItemService;
import org.dcsa.core.events.model.CargoItem;
import org.dcsa.core.events.model.UtilizedTransportEquipment;
import org.dcsa.core.events.model.mapper.CargoItemMapper;
import org.dcsa.core.events.model.mapper.CargoLineItemMapper;
import org.dcsa.core.events.model.transferobjects.CargoItemTO;
import org.dcsa.core.events.model.transferobjects.UtilizedTransportEquipmentTO;
import org.dcsa.core.events.repository.*;
import org.dcsa.core.events.service.ReferenceService;
import org.dcsa.core.exception.ConcreteRequestErrorMessageException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ConsignmentItemServiceImpl implements ConsignmentItemService {
  // Services
  private final ReferenceService referenceService;

  // Repositories
  private final ConsignmentItemRepository consignmentItemRepository;
  private final CargoItemRepository cargoItemRepository;
  private final CargoLineItemRepository cargoLineItemRepository;
  private final ShipmentRepository shipmentRepository;
  private final ReferenceRepository referenceRepository;
  private final UtilizedTransportEquipmentRepository utilizedTransportEquipmentRepository;

  // Mappers
  private final ConsignmentItemMapper consignmentItemMapper;
  private final CargoLineItemMapper cargoLineItemMapper;
  private final CargoItemMapper cargoItemMapper;

  @Override
  public Mono<List<ConsignmentItemTO>> fetchConsignmentItemsTOByShippingInstructionReference(
      String shippingInstructionReference) {

    return consignmentItemRepository
        .findAllByShippingInstructionID(shippingInstructionReference)
        .switchIfEmpty(
            Mono.error(
                ConcreteRequestErrorMessageException.notFound(
                    "Could not find Consignment Items for the shipping instruction")))
        .flatMap(
            ci -> {
              ConsignmentItemTO.ConsignmentItemTOBuilder ciTo =
                  consignmentItemMapper.consignmentItemToDTO(ci).toBuilder();

              return Mono.when(
                      referenceService
                          .findByShippingInstructionReference(shippingInstructionReference)
                          .doOnNext(ciTo::references),
                      cargoItemRepository
                          .findAllByConsignmentItemID(ci.getId())
                          .switchIfEmpty(
                              Flux.error(
                                  ConcreteRequestErrorMessageException.notFound(
                                      "Could not find cargo items")))
                          .flatMap(
                              cargoItem ->
                                  Mono.zip(
                                      utilizedTransportEquipmentRepository
                                          .findUtilizedTransportEquipmentsByShippingInstructionReference(
                                              shippingInstructionReference)
                                          .collectList()
                                          .flatMap(
                                              utilizedTransportEquipments -> {
                                                Map<UUID, String> equipments =
                                                    utilizedTransportEquipments.stream()
                                                        .collect(
                                                            Collectors.toMap(
                                                                UtilizedTransportEquipment::getId,
                                                                UtilizedTransportEquipment
                                                                    ::getEquipmentReference));

                                                return Mono.justOrEmpty(
                                                        equipments.get(
                                                            cargoItem
                                                                .getUtilizedTransportEquipmentID()))
                                                    .switchIfEmpty(
                                                        Mono.error(
                                                            ConcreteRequestErrorMessageException
                                                                .notFound(
                                                                    "Could not find equipment reference for one of the cargoItems")));
                                              }),
                                      Mono.just(cargoItem)))
                          .map(
                              tuple -> cargoItemMapper.cargoItemToDto(tuple.getT2(), tuple.getT1()))
                          .collectList()
                          .doOnNext(ciTo::cargoItems))
                  .thenReturn(ciTo);
            })
        .flatMap(x -> Mono.just(x.build()))
        .collectList();
  }

  @Override
  public Mono<List<ConsignmentItemTO>> createConsignmentItemsByShippingInstructionReferenceAndTOs(
      String shippingInstructionReference,
      List<ConsignmentItemTO> consignmentItemTOs,
      List<UtilizedTransportEquipmentTO> utilizedTransportEquipmentTOs) {
    if (shippingInstructionReference == null)
      return Mono.error(
          ConcreteRequestErrorMessageException.invalidParameter(
              "ShippingInstructionReference cannot be null"));

    if (Objects.isNull(consignmentItemTOs) || consignmentItemTOs.isEmpty()) {
      return Mono.empty();
    }

    return Flux.fromIterable(consignmentItemTOs)
        .flatMap(
            consignmentItemTO -> {
              ConsignmentItemTO.ConsignmentItemTOBuilder consignmentItemTOBuilder =
                  consignmentItemTO.toBuilder();
              return shipmentRepository
                  .findByCarrierBookingReference(consignmentItemTO.getCarrierBookingReference())
                  .switchIfEmpty(
                      Mono.error(
                          ConcreteRequestErrorMessageException.notFound(
                              "No shipment found with carrierBookingReference: "
                                  + consignmentItemTO.getCarrierBookingReference())))
                  .flatMap(
                      shipment ->
                          consignmentItemRepository
                              .save(
                                  consignmentItemMapper
                                      .dtoToConsignmentItemWithShippingReferenceAndShipmentId(
                                          consignmentItemTO,
                                          shippingInstructionReference,
                                          shipment.getShipmentID()))
                              .flatMap(
                                  consignmentItem ->
                                      Mono.when(
                                              referenceService
                                                  .createReferencesByShippingInstructionReferenceAndConsignmentIdAndTOs(
                                                      shippingInstructionReference,
                                                      consignmentItem.getId(),
                                                      consignmentItemTO.getReferences())
                                                  .doOnNext(consignmentItemTOBuilder::references),
                                              saveCargoItems(
                                                      shippingInstructionReference,
                                                      consignmentItem.getId(),
                                                      consignmentItemTO.getCargoItems(),
                                                      utilizedTransportEquipmentTOs)
                                                  .doOnNext(consignmentItemTOBuilder::cargoItems))
                                          .thenReturn(consignmentItem)))
                  .thenReturn(consignmentItemTOBuilder.build());
            })
        .collectList();
  }

  @Override
  public Mono<Void> removeConsignmentItemsByShippingInstructionReference(
      String shippingInstructionReference) {
    if (shippingInstructionReference == null)
      return Mono.error(
          ConcreteRequestErrorMessageException.invalidParameter(
              "ShippingInstructionReference cannot be null"));

    return cargoItemRepository
        .findAllByShippingInstructionReference(shippingInstructionReference)
        .flatMap(
            cargoItem ->
                cargoLineItemRepository
                    .deleteByCargoItemID(cargoItem.getId())
                    .thenReturn(cargoItem))
        .flatMap(
            cargoItem -> cargoItemRepository.deleteById(cargoItem.getId()).thenReturn(cargoItem))
        .flatMap(
            ignored ->
                consignmentItemRepository
                    .findAllByShippingInstructionID((shippingInstructionReference))
                    .flatMap(
                        consignmentItem ->
                            Mono.when(
                                    referenceRepository.deleteByConsignmentItemID(
                                        consignmentItem.getId()),
                                    consignmentItemRepository.deleteById(consignmentItem.getId()))
                                .thenReturn(consignmentItem)))
        .then(Mono.empty());
  }

  private Mono<List<CargoItemTO>> saveCargoItems(
      String shippingInstructionReference,
      UUID consignmentId,
      List<CargoItemTO> cargoItemTOs,
      List<UtilizedTransportEquipmentTO> utilizedTransportEquipmentTOs) {

    if (Objects.isNull(cargoItemTOs) || cargoItemTOs.isEmpty()) {
      return Mono.just(Collections.emptyList());
    }
    return Flux.fromIterable(cargoItemTOs)
        .flatMap(
            cargoItemTO -> {
              Optional<UtilizedTransportEquipmentTO> equipment =
                  utilizedTransportEquipmentTOs.stream()
                      .filter(
                          x ->
                              x.getEquipment()
                                  .getEquipmentReference()
                                  .equals(cargoItemTO.getEquipmentReference()))
                      .findFirst();
              if (equipment.isEmpty()) {
                return Mono.error(
                    ConcreteRequestErrorMessageException.notFound(
                        "Could not find utilizedTransportEquipment from equipment reference: "
                            + cargoItemTO.getEquipmentReference()));
              }

              UUID utilizedTransportEquipmentId = equipment.get().getId();
              CargoItem cargoItem =
                  cargoItemMapper.dtoToCargoItemWithConsignmentIdAndShippingInstructionReference(
                      cargoItemTO, consignmentId, shippingInstructionReference);
              cargoItem.setUtilizedTransportEquipmentID(utilizedTransportEquipmentId);
              return cargoItemRepository
                  .save(cargoItem)
                  .map(CargoItem::getId)
                  .zipWith(Mono.just(cargoItemTO))
                  .flatMap(t -> saveCargoLineItems(t.getT1(), cargoItemTO));
            })
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
