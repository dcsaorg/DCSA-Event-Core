package org.dcsa.core.events.service.impl;

import org.dcsa.core.events.edocumentation.model.ConsignmentItem;
import org.dcsa.core.events.edocumentation.model.mapper.ConsignmentItemMapper;
import org.dcsa.core.events.edocumentation.model.transferobject.ConsignmentItemTO;
import org.dcsa.core.events.edocumentation.repository.ConsignmentItemRepository;
import org.dcsa.core.events.edocumentation.service.impl.ConsignmentItemServiceImpl;
import org.dcsa.core.events.model.*;
import org.dcsa.core.events.model.enums.ReferenceTypeCode;
import org.dcsa.core.events.model.enums.ShipmentEventTypeCode;
import org.dcsa.core.events.model.enums.VolumeUnit;
import org.dcsa.core.events.model.enums.WeightUnit;
import org.dcsa.core.events.model.mapper.CargoItemMapper;
import org.dcsa.core.events.model.mapper.CargoLineItemMapper;
import org.dcsa.core.events.model.transferobjects.*;
import org.dcsa.core.events.repository.CargoItemRepository;
import org.dcsa.core.events.repository.CargoLineItemRepository;
import org.dcsa.core.events.repository.ReferenceRepository;
import org.dcsa.core.events.repository.ShipmentRepository;
import org.dcsa.core.events.service.ReferenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test for ConsignmentItemService implementation")
class ConsignmentItemServiceImplTest {

  @Mock CargoItemRepository cargoItemRepository;
  @Mock CargoLineItemRepository cargoLineItemRepository;
  @Mock ShipmentRepository shipmentRepository;
  @Mock ReferenceService referenceService;
  @Mock ConsignmentItemRepository consignmentItemRepository;
  @Mock ReferenceRepository referenceRepository;

  @Spy
  ConsignmentItemMapper consignmentItemMapper = Mappers.getMapper((ConsignmentItemMapper.class));

  @Spy CargoLineItemMapper cargoLineItemMapper = Mappers.getMapper(CargoLineItemMapper.class);
  @Spy CargoItemMapper cargoItemMapper = Mappers.getMapper(CargoItemMapper.class);

  @InjectMocks ConsignmentItemServiceImpl consignmentItemService;

  CargoLineItem cargoLineItem;
  CargoItem cargoItem;
  Equipment equipment;
  UtilizedTransportEquipment utilizedTransportEquipment;
  Shipment shipment;
  ConsignmentItem consignmentItem;
  ShippingInstruction shippingInstruction;

  UtilizedTransportEquipmentTO utilizedTransportEquipmentTO;
  ReferenceTO referenceTO;
  ConsignmentItemTO consignmentItemTO;
  CargoItemTO cargoItemTO;

  @BeforeEach
  void init() {
    initEntities();
    initTOs();
  }

  private void initEntities() {
    shipment = new Shipment();
    shipment.setShipmentID(UUID.randomUUID());
    shipment.setCarrierBookingReference("carrierBookingReference1");

    equipment = new Equipment();
    equipment.setEquipmentReference(UUID.randomUUID().toString().substring(0, 15));
    equipment.setIsoEquipmentCode("ISO1");
    equipment.setWeightUnit("KGM");
    equipment.setTareWeight(120F);

    utilizedTransportEquipment = new UtilizedTransportEquipment();
    utilizedTransportEquipment.setId(UUID.randomUUID());
    utilizedTransportEquipment.setCargoGrossWeight(120.0F);
    utilizedTransportEquipment.setCargoGrossWeightUnit(WeightUnit.KGM);
    utilizedTransportEquipment.setShipmentID(shipment.getShipmentID());
    utilizedTransportEquipment.setEquipmentReference(equipment.getEquipmentReference());
    utilizedTransportEquipment.setIsShipperOwned(true);

    cargoItem = new CargoItem();
    cargoItem.setId(UUID.randomUUID());
    cargoItem.setUtilizedTransportEquipmentID(utilizedTransportEquipment.getId());
    cargoItem.setShippingInstructionReference(UUID.randomUUID().toString());
    cargoItem.setPackageCode("ABC");
    cargoItem.setNumberOfPackages(1);
    cargoItem.setWeight(100F);
    cargoItem.setWeightUnit(WeightUnit.KGM);
    cargoItem.setVolume(400F);
    cargoItem.setVolumeUnit(VolumeUnit.CBM);

    cargoLineItem = new CargoLineItem();
    cargoLineItem.setCargoItemID(cargoItem.getId());
    cargoLineItem.setCargoLineItemID("CargoLineItem");
    cargoLineItem.setShippingMarks("shippingMarks");

    OffsetDateTime now = OffsetDateTime.now();
    shippingInstruction = new ShippingInstruction();
    shippingInstruction.setShippingInstructionReference(UUID.randomUUID().toString());
    shippingInstruction.setDocumentStatus(ShipmentEventTypeCode.RECE);
    shippingInstruction.setShippingInstructionCreatedDateTime(now);
    shippingInstruction.setShippingInstructionUpdatedDateTime(now);

    consignmentItem = new ConsignmentItem();
    consignmentItem.setShippingInstructionID(shippingInstruction.getShippingInstructionReference());
    consignmentItem.setId(UUID.randomUUID());
    consignmentItem.setVolume(12.02);
    consignmentItem.setHsCode("411510");
    consignmentItem.setWeight(24.08);
    consignmentItem.setWeightUnit(WeightUnit.KGM);
    consignmentItem.setVolumeUnit(VolumeUnit.CBM);
    consignmentItem.setShipmentID(shipment.getShipmentID());
  }

  private void initTOs() {

    CargoLineItemTO cargoLineItemTO = new CargoLineItemTO();
    cargoLineItemTO.setCargoLineItemID("CargoLineItem");
    cargoLineItemTO.setShippingMarks("shippingMarks");

    referenceTO = new ReferenceTO();
    referenceTO.setReferenceValue("referenceValue");
    referenceTO.setReferenceType(ReferenceTypeCode.FF);

    EquipmentTO equipmentTO = new EquipmentTO();
    equipmentTO.setEquipmentReference(equipment.getEquipmentReference());
    equipmentTO.setIsoEquipmentCode("ISO1");
    equipmentTO.setTareWeight(120F);
    equipmentTO.setWeightUnit(WeightUnit.KGM);

    utilizedTransportEquipmentTO = new UtilizedTransportEquipmentTO();
    utilizedTransportEquipmentTO.setCarrierBookingReference("carrierBookingReference1");
    utilizedTransportEquipmentTO.setCargoGrossWeight(120.0F);
    utilizedTransportEquipmentTO.setCargoGrossWeightUnit(WeightUnit.KGM);
    utilizedTransportEquipmentTO.setEquipment(equipmentTO);

    cargoItemTO = new CargoItemTO();
    cargoItemTO.setCargoLineItems(Collections.singletonList(cargoLineItemTO));
    cargoItemTO.setWeight(100F);
    cargoItemTO.setWeightUnit(WeightUnit.KGM);
    cargoItemTO.setVolume(400F);
    cargoItemTO.setVolumeUnit(VolumeUnit.CBM);
    cargoItemTO.setPackageCode("ABC");
    cargoItemTO.setNumberOfPackages(1);
    cargoItemTO.setReferences(List.of(referenceTO));
    cargoItemTO.setEquipmentReference(equipmentTO.getEquipmentReference());

    ConsignmentItemTO.ConsignmentItemTOBuilder consignmentItemTOBuilder =
        ConsignmentItemTO.builder();
    consignmentItemTOBuilder.weight(12.02);
    consignmentItemTOBuilder.descriptionOfGoods("It's some nice... shoes?");
    consignmentItemTOBuilder.cargoItems(List.of(cargoItemTO));
    consignmentItemTOBuilder.references(List.of(referenceTO));
    consignmentItemTO = consignmentItemTOBuilder.build();
  }

  @Nested
  @DisplayName(
      "Tests for the method createConsignmentItemsByShippingInstructionReferenceAndTOs(#shippingInstructionReference, #consignmentItemTOs, #utilizedTransportEquipmentTOs)")
  class testCreateConsignmentItem {

    @Test
    @DisplayName("Test create testCreateConsignmentItems")
    void testCreateConsignmentItems() {
      String shippingInstructionReference = shippingInstruction.getShippingInstructionReference();

      when(shipmentRepository.findByCarrierBookingReference(any())).thenReturn(Mono.just(shipment));
      when(consignmentItemRepository.save(any())).thenReturn(Mono.just(consignmentItem));
      when(cargoItemRepository.save(any())).thenReturn(Mono.just(cargoItem));
      when(cargoLineItemRepository.save(any())).thenReturn(Mono.just(cargoLineItem));
      when(referenceService.createReferencesByShippingInstructionReferenceAndTOs(
              eq(shippingInstructionReference), any()))
          .thenReturn(Mono.just(List.of(referenceTO)));
      when(referenceService.createReferencesByShippingInstructionReferenceAndConsignmentIdAndTOs(
              eq(shippingInstructionReference), any(), any()))
          .thenReturn(Mono.just(List.of(referenceTO)));

      ArgumentCaptor<CargoItem> argumentCaptorCargoItem = ArgumentCaptor.forClass(CargoItem.class);
      StepVerifier.create(
              consignmentItemService.createConsignmentItemsByShippingInstructionReferenceAndTOs(
                  shippingInstructionReference,
                  Collections.singletonList(consignmentItemTO),
                  Collections.singletonList(utilizedTransportEquipmentTO)))
          .assertNext(
              consignmentItemTOs -> {
                verify(cargoItemRepository).save(argumentCaptorCargoItem.capture());
                verify(cargoLineItemRepository).save(any());
                verify(referenceService)
                    .createReferencesByShippingInstructionReferenceAndTOs(any(), any());
                verify(referenceService)
                    .createReferencesByShippingInstructionReferenceAndConsignmentIdAndTOs(
                        any(), any(), any());
                assertNotNull(consignmentItemTOs);
                assertTrue(consignmentItemTOs.stream().findFirst().isPresent());
                assertNotNull(consignmentItemTOs.stream().findFirst().get().getCargoItems());
                assertNotNull(consignmentItemTOs.stream().findFirst().get().getReferences());
                assertEquals(
                    consignmentItemTO.getHsCode(),
                    consignmentItemTOs.stream().findFirst().get().getHsCode());
                assertEquals(
                    consignmentItemTO.getDescriptionOfGoods(),
                    consignmentItemTOs.stream().findFirst().get().getDescriptionOfGoods());

                assertEquals(
                    shippingInstructionReference,
                    argumentCaptorCargoItem.getValue().getShippingInstructionReference());
                assertTrue(
                    consignmentItemTOs.stream().findFirst().get().getCargoItems().stream()
                        .findFirst()
                        .isPresent());
                assertEquals(
                    equipment.getEquipmentReference(),
                    consignmentItemTOs.stream().findFirst().get().getCargoItems().stream()
                        .findFirst()
                        .get()
                        .getEquipmentReference());
                assertEquals(
                    utilizedTransportEquipmentTO.getId(),
                    argumentCaptorCargoItem.getValue().getUtilizedTransportEquipmentID());
              })
          .verifyComplete();
    }

    @Test
    @DisplayName("Test create testCreateConsignmentItemsWithoutReferences")
    void testCreateConsignmentItemsWithoutReferences() {

      ConsignmentItemTO.ConsignmentItemTOBuilder consignmentItemTOBuilder =
          consignmentItemTO.toBuilder();
      consignmentItemTOBuilder.references(null);
      consignmentItemTO = consignmentItemTOBuilder.build();

      String shippingInstructionReference = shippingInstruction.getShippingInstructionReference();

      when(shipmentRepository.findByCarrierBookingReference(any())).thenReturn(Mono.just(shipment));
      when(consignmentItemRepository.save(any())).thenReturn(Mono.just(consignmentItem));
      when(cargoItemRepository.save(any())).thenReturn(Mono.just(cargoItem));
      when(cargoLineItemRepository.save(any())).thenReturn(Mono.just(cargoLineItem));
      when(referenceService.createReferencesByShippingInstructionReferenceAndTOs(
              eq(shippingInstructionReference), any()))
          .thenReturn(Mono.just(List.of(referenceTO)));
      when(referenceService.createReferencesByShippingInstructionReferenceAndConsignmentIdAndTOs(
              eq(shippingInstructionReference), any(), any()))
          .thenReturn(Mono.just(List.of(referenceTO)));

      ArgumentCaptor<CargoItem> argumentCaptorCargoItem = ArgumentCaptor.forClass(CargoItem.class);
      StepVerifier.create(
              consignmentItemService.createConsignmentItemsByShippingInstructionReferenceAndTOs(
                  shippingInstructionReference,
                  Collections.singletonList(consignmentItemTO),
                  Collections.singletonList(utilizedTransportEquipmentTO)))
          .assertNext(
              consignmentItemTOs -> {
                verify(cargoItemRepository).save(argumentCaptorCargoItem.capture());
                verify(cargoLineItemRepository).save(any());
                assertNotNull(consignmentItemTOs);
                assertTrue(consignmentItemTOs.stream().findFirst().isPresent());
                assertNotNull(consignmentItemTOs.stream().findFirst().get().getCargoItems());
                assertNull(consignmentItemTOs.stream().findFirst().get().getReferences());
                assertEquals(
                    consignmentItemTO.getHsCode(),
                    consignmentItemTOs.stream().findFirst().get().getHsCode());
                assertEquals(
                    consignmentItemTO.getDescriptionOfGoods(),
                    consignmentItemTOs.stream().findFirst().get().getDescriptionOfGoods());

                assertEquals(
                    shippingInstructionReference,
                    argumentCaptorCargoItem.getValue().getShippingInstructionReference());
                assertTrue(
                    consignmentItemTOs.stream().findFirst().get().getCargoItems().stream()
                        .findFirst()
                        .isPresent());
                assertEquals(
                    equipment.getEquipmentReference(),
                    consignmentItemTOs.stream().findFirst().get().getCargoItems().stream()
                        .findFirst()
                        .get()
                        .getEquipmentReference());
                assertEquals(
                    utilizedTransportEquipmentTO.getId(),
                    argumentCaptorCargoItem.getValue().getUtilizedTransportEquipmentID());
              })
          .verifyComplete();
    }

    @Test
    @DisplayName("Test create testCreateConsignmentItemsWithoutCargoLineItems")
    void testCreateConsignmentItemsWithoutCargoLineItems() {
      cargoItemTO.setCargoLineItems(null);
      ConsignmentItemTO.ConsignmentItemTOBuilder consignmentItemTOBuilder =
          consignmentItemTO.toBuilder();
      consignmentItemTOBuilder.cargoItems(List.of(cargoItemTO));
      consignmentItemTO = consignmentItemTOBuilder.build();

      String shippingInstructionReference = shippingInstruction.getShippingInstructionReference();

      when(shipmentRepository.findByCarrierBookingReference(any())).thenReturn(Mono.just(shipment));
      when(consignmentItemRepository.save(any())).thenReturn(Mono.just(consignmentItem));
      when(cargoItemRepository.save(any())).thenReturn(Mono.just(cargoItem));
      when(referenceService.createReferencesByShippingInstructionReferenceAndConsignmentIdAndTOs(
              eq(shippingInstructionReference), any(), any()))
          .thenReturn(Mono.just(List.of(referenceTO)));

      ArgumentCaptor<CargoItem> argumentCaptorCargoItem = ArgumentCaptor.forClass(CargoItem.class);
      StepVerifier.create(
              consignmentItemService.createConsignmentItemsByShippingInstructionReferenceAndTOs(
                  shippingInstructionReference,
                  Collections.singletonList(consignmentItemTO),
                  Collections.singletonList(utilizedTransportEquipmentTO)))
          .assertNext(
              consignmentItemTOs -> {
                verify(cargoItemRepository).save(argumentCaptorCargoItem.capture());
                verify(referenceService)
                    .createReferencesByShippingInstructionReferenceAndConsignmentIdAndTOs(
                        any(), any(), any());
                assertNotNull(consignmentItemTOs);
                assertTrue(consignmentItemTOs.stream().findFirst().isPresent());
                assertNotNull(consignmentItemTOs.stream().findFirst().get().getCargoItems());
                assertNotNull(consignmentItemTOs.stream().findFirst().get().getReferences());
                assertEquals(
                    consignmentItemTO.getHsCode(),
                    consignmentItemTOs.stream().findFirst().get().getHsCode());
                assertEquals(
                    consignmentItemTO.getDescriptionOfGoods(),
                    consignmentItemTOs.stream().findFirst().get().getDescriptionOfGoods());

                assertEquals(
                    shippingInstructionReference,
                    argumentCaptorCargoItem.getValue().getShippingInstructionReference());
                assertTrue(
                    consignmentItemTOs.stream().findFirst().get().getCargoItems().stream()
                        .findFirst()
                        .isPresent());
                assertEquals(
                    equipment.getEquipmentReference(),
                    consignmentItemTOs.stream().findFirst().get().getCargoItems().stream()
                        .findFirst()
                        .get()
                        .getEquipmentReference());
                assertEquals(
                    utilizedTransportEquipmentTO.getId(),
                    argumentCaptorCargoItem.getValue().getUtilizedTransportEquipmentID());
              })
          .verifyComplete();
    }

    @Test
    @DisplayName("Test create testCreateConsignmentItemsWithoutCargoLineItemsAndReferences")
    void testCreateConsignmentItemsWithoutCargoLineItemsAndReferences() {
      cargoItemTO.setCargoLineItems(null);
      ConsignmentItemTO.ConsignmentItemTOBuilder consignmentItemTOBuilder =
          consignmentItemTO.toBuilder();
      consignmentItemTOBuilder.cargoItems(List.of(cargoItemTO));
      consignmentItemTOBuilder.references(null);
      consignmentItemTO = consignmentItemTOBuilder.build();

      String shippingInstructionReference = shippingInstruction.getShippingInstructionReference();

      when(shipmentRepository.findByCarrierBookingReference(any())).thenReturn(Mono.just(shipment));
      when(consignmentItemRepository.save(any())).thenReturn(Mono.just(consignmentItem));
      when(cargoItemRepository.save(any())).thenReturn(Mono.just(cargoItem));
      when(referenceService.createReferencesByShippingInstructionReferenceAndConsignmentIdAndTOs(
              eq(shippingInstructionReference), any(), any()))
          .thenReturn(Mono.just(List.of(referenceTO)));

      ArgumentCaptor<CargoItem> argumentCaptorCargoItem = ArgumentCaptor.forClass(CargoItem.class);
      StepVerifier.create(
              consignmentItemService.createConsignmentItemsByShippingInstructionReferenceAndTOs(
                  shippingInstructionReference,
                  Collections.singletonList(consignmentItemTO),
                  Collections.singletonList(utilizedTransportEquipmentTO)))
          .assertNext(
              consignmentItemTOs -> {
                verify(cargoItemRepository).save(argumentCaptorCargoItem.capture());
                verify(referenceService)
                    .createReferencesByShippingInstructionReferenceAndConsignmentIdAndTOs(
                        any(), any(), any());
                assertNotNull(consignmentItemTOs);
                assertTrue(consignmentItemTOs.stream().findFirst().isPresent());
                assertNotNull(consignmentItemTOs.stream().findFirst().get().getCargoItems());
                assertEquals(
                    consignmentItemTO.getHsCode(),
                    consignmentItemTOs.stream().findFirst().get().getHsCode());
                assertEquals(
                    consignmentItemTO.getDescriptionOfGoods(),
                    consignmentItemTOs.stream().findFirst().get().getDescriptionOfGoods());

                assertEquals(
                    shippingInstructionReference,
                    argumentCaptorCargoItem.getValue().getShippingInstructionReference());
                assertTrue(
                    consignmentItemTOs.stream().findFirst().get().getCargoItems().stream()
                        .findFirst()
                        .isPresent());
                assertEquals(
                    equipment.getEquipmentReference(),
                    consignmentItemTOs.stream().findFirst().get().getCargoItems().stream()
                        .findFirst()
                        .get()
                        .getEquipmentReference());
                assertEquals(
                    utilizedTransportEquipmentTO.getId(),
                    argumentCaptorCargoItem.getValue().getUtilizedTransportEquipmentID());
              })
          .verifyComplete();
    }
  }

  @Nested
  @DisplayName(
      "Tests for the method removeConsignmentItemsByShippingInstructionReferenceAndTOs(#shippingInstructionReference, #consignmentItemTOs, #utilizedTransportEquipmentTOs)")
  class testRemoveConsignmentItem {

    @Test
    @DisplayName("Test removeConsignmentItemsByShippingInstructionReference")
    void testRemoveConsignmentItems() {
      String shippingInstructionReference = shippingInstruction.getShippingInstructionReference();

      when(cargoItemRepository.findAllByShippingInstructionReference(any()))
          .thenReturn(Flux.just(cargoItem));
      when(cargoLineItemRepository.deleteByCargoItemID(any())).thenReturn(Mono.empty());
      when(cargoItemRepository.deleteById(any(UUID.class))).thenReturn(Mono.empty());
      when(consignmentItemRepository.findAllByShippingInstructionID(any()))
          .thenReturn(Flux.just(consignmentItem));
      when(referenceRepository.deleteByConsignmentItemID(any(UUID.class))).thenReturn(Mono.empty());
      when(consignmentItemRepository.deleteById(any(UUID.class))).thenReturn(Mono.empty());

      StepVerifier.create(
              consignmentItemService.removeConsignmentItemsByShippingInstructionReference(
                  shippingInstructionReference))
          .assertNext(
              consignmentItemTOs -> {
                verify(cargoLineItemRepository).deleteByCargoItemID(any());
                verify(consignmentItemRepository).findAllByShippingInstructionID(any());
                verify(referenceRepository).deleteByConsignmentItemID(any(UUID.class));
                verify(consignmentItemRepository).deleteById(any(UUID.class));
                verify(cargoItemRepository).findAllByShippingInstructionReference(any());
                verify(cargoItemRepository).deleteById(any(UUID.class));
                assertEquals(shippingInstructionReference, consignmentItemTOs);
              })
          .verifyComplete();
    }
  }
}
