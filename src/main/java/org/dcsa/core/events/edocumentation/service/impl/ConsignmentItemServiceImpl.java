package org.dcsa.core.events.edocumentation.service.impl;

import lombok.AllArgsConstructor;
import org.dcsa.core.events.edocumentation.model.mapper.ConsignmentItemMapper;
import org.dcsa.core.events.edocumentation.model.transferobject.ConsignmentItemTO;
import org.dcsa.core.events.edocumentation.repository.ConsignmentItemRepository;
import org.dcsa.core.events.edocumentation.service.ConsignmentItemService;
import org.dcsa.core.events.model.CargoItem;
import org.dcsa.core.events.model.mapper.CargoItemMapper;
import org.dcsa.core.events.model.mapper.CargoLineItemMapper;
import org.dcsa.core.events.model.transferobjects.CargoItemTO;
import org.dcsa.core.events.model.transferobjects.EquipmentTO;
import org.dcsa.core.events.model.transferobjects.UtilizedTransportEquipmentTO;
import org.dcsa.core.events.repository.CargoItemRepository;
import org.dcsa.core.events.repository.CargoLineItemRepository;
import org.dcsa.core.events.repository.ShipmentRepository;
import org.dcsa.core.events.repository.UtilizedTransportEquipmentRepository;
import org.dcsa.core.events.service.ReferenceService;
import org.dcsa.core.exception.ConcreteRequestErrorMessageException;
import org.dcsa.core.util.MappingUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
  private final UtilizedTransportEquipmentRepository utilizedTransportEquipmentRepository;

  // Mappers
  private final ConsignmentItemMapper consignmentItemMapper;
  private final CargoLineItemMapper cargoLineItemMapper;
  private final CargoItemMapper cargoItemMapper;

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
            consignmentItemTO ->
                shipmentRepository
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
                                                        consignmentItemTO.getReferences()),
                                                saveCargoItems(
                                                    shippingInstructionReference,
                                                    consignmentItem.getId(),
                                                    consignmentItemTO.getCargoItems(),
                                                    utilizedTransportEquipmentTOs))
                                            .thenReturn(consignmentItem))))
        .buffer(MappingUtils.SQL_LIST_BUFFER_SIZE) // process in smaller batches
        .concatMap(consignmentItemRepository::saveAll)
        .map(consignmentItemMapper::consignmentItemToDTO)
        .collectList();
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
