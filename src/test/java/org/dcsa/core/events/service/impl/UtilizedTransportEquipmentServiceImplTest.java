package org.dcsa.core.events.service.impl;

import org.dcsa.core.events.model.*;
import org.dcsa.core.events.model.enums.*;
import org.dcsa.core.events.model.mapper.*;
import org.dcsa.core.events.model.transferobjects.*;
import org.dcsa.core.events.repository.*;
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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test for UtilizedTransportEquipmentService implementation")
class UtilizedTransportEquipmentServiceImplTest {

  @Mock UtilizedTransportEquipmentRepository utilizedTransportEquipmentRepository;
  @Mock EquipmentRepository equipmentRepository;
  @Mock SealRepository sealRepository;
  @Mock ActiveReeferSettingsRepository activeReeferSettingsRepository;
  @Mock CargoItemRepository cargoItemRepository;
  @Mock CargoLineItemRepository cargoLineItemRepository;
  @Mock ShipmentRepository shipmentRepository;
  @Mock ReferenceService referenceService;

  @Spy SealMapper sealMapper = Mappers.getMapper(SealMapper.class);
  @Spy CargoLineItemMapper cargoLineItemMapper = Mappers.getMapper(CargoLineItemMapper.class);
  @Spy CargoItemMapper cargoItemMapper = Mappers.getMapper(CargoItemMapper.class);

  @Spy
  ActiveReeferSettingsMapper activeReeferSettingsMapper =
      Mappers.getMapper(ActiveReeferSettingsMapper.class);

  @Spy EquipmentMapper equipmentMapper = Mappers.getMapper(EquipmentMapper.class);

  @Spy
  UtilizedTransportEquipmentMapper utilizedTransportEquipmentMapper =
      Mappers.getMapper(UtilizedTransportEquipmentMapper.class);

  @InjectMocks UtilizedTransportEquipmentServiceImpl utilizedTransportEquipmentService;

  CargoLineItem cargoLineItem;
  CargoItem cargoItem;
  Seal seal;
  ActiveReeferSettings activeReeferSettings;
  Equipment equipment;
  UtilizedTransportEquipment utilizedTransportEquipment;
  Shipment shipment;
  UtilizedTransportEquipmentTO utilizedTransportEquipmentTO;
  ReferenceTO referenceTO;

  @BeforeEach
  void init() {
    initEntities(
        UUID.randomUUID(),
        UUID.randomUUID(),
        "equipmentReference1",
        "shippingInstructionReference1");
    initTOs();
  }

  private void initEntities(
      UUID shipmentID,
      UUID utilizedTransportEquipmentID,
      String equipmentReference,
      String shippingInstructionReference) {
    utilizedTransportEquipment = new UtilizedTransportEquipment();
    utilizedTransportEquipment.setId(utilizedTransportEquipmentID);
    utilizedTransportEquipment.setCargoGrossWeight(120.0F);
    utilizedTransportEquipment.setCargoGrossWeightUnit(WeightUnit.KGM);
    utilizedTransportEquipment.setShipmentID(shipmentID);
    utilizedTransportEquipment.setEquipmentReference(equipmentReference);
    utilizedTransportEquipment.setIsShipperOwned(true);

    equipment = new Equipment();
    equipment.setEquipmentReference(equipmentReference);
    equipment.setIsoEquipmentCode("ISO1");
    equipment.setWeightUnit("KGM");
    equipment.setTareWeight(120F);

    activeReeferSettings = new ActiveReeferSettings();
    activeReeferSettings.setUtilizedTransportEquipmentID(utilizedTransportEquipmentID);
    activeReeferSettings.setVentilationMax(10F);
    activeReeferSettings.setVentilationMin(5F);
    activeReeferSettings.setTemperatureUnit(TemperatureUnit.CEL);
    activeReeferSettings.setTemperatureMax(8F);
    activeReeferSettings.setTemperatureMin(5F);
    activeReeferSettings.setHumidityMin(30F);
    activeReeferSettings.setHumidityMax(50F);

    seal = new Seal();
    seal.setId(UUID.randomUUID());
    seal.setUtilizedTransportEquipmentID(utilizedTransportEquipmentID);
    seal.setSealType("BLT");
    seal.setSealSource("CAR");
    seal.setSealNumber("1");

    cargoItem = new CargoItem();
    cargoItem.setId(UUID.randomUUID());
    cargoItem.setUtilizedTransportEquipmentID(utilizedTransportEquipmentID);
    cargoItem.setShippingInstructionReference(shippingInstructionReference);
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

    shipment = new Shipment();
    shipment.setShipmentID(shipmentID);
    shipment.setCarrierBookingReference("carrierBookingReference1");
  }

  private void initTOs() {

    CargoLineItemTO cargoLineItemTO = new CargoLineItemTO();
    cargoLineItemTO.setCargoLineItemID("CargoLineItem");
    cargoLineItemTO.setShippingMarks("shippingMarks");

    referenceTO = new ReferenceTO();
    referenceTO.setReferenceValue("referenceValue");
    referenceTO.setReferenceType(ReferenceTypeCode.FF);

    CargoItemTO cargoItemTO = new CargoItemTO();
    cargoItemTO.setCargoLineItems(Collections.singletonList(cargoLineItemTO));
    cargoItemTO.setWeight(100F);
    cargoItemTO.setWeightUnit(WeightUnit.KGM);
    cargoItemTO.setVolume(400F);
    cargoItemTO.setVolumeUnit(VolumeUnit.CBM);
    cargoItemTO.setPackageCode("ABC");
    cargoItemTO.setNumberOfPackages(1);
    cargoItemTO.setReferences(List.of(referenceTO));

    SealTO sealTO = new SealTO();
    sealTO.setSealNumber("1");
    sealTO.setSealType(SealTypeCode.BLT);
    sealTO.setSealSource(SealSourceCode.CAR);

    ActiveReeferSettingsTO activeReeferSettingsTO = new ActiveReeferSettingsTO();
    activeReeferSettingsTO.setVentilationMax(10F);
    activeReeferSettingsTO.setVentilationMin(5F);
    activeReeferSettingsTO.setTemperatureUnit(TemperatureUnit.CEL);
    activeReeferSettingsTO.setTemperatureMax(8F);
    activeReeferSettingsTO.setTemperatureMin(5F);
    activeReeferSettingsTO.setHumidityMin(30F);
    activeReeferSettingsTO.setHumidityMax(50F);

    EquipmentTO equipmentTO = new EquipmentTO();
    equipmentTO.setEquipmentReference("equipmentReference1");
    equipmentTO.setIsoEquipmentCode("ISO1");
    equipmentTO.setTareWeight(120F);
    equipmentTO.setWeightUnit(WeightUnit.KGM);

    utilizedTransportEquipmentTO = new UtilizedTransportEquipmentTO();
    utilizedTransportEquipmentTO.setCarrierBookingReference("carrierBookingReference1");
    utilizedTransportEquipmentTO.setCargoGrossWeight(120.0F);
    utilizedTransportEquipmentTO.setCargoGrossWeightUnit(WeightUnit.KGM);
    utilizedTransportEquipmentTO.setEquipment(equipmentTO);
    utilizedTransportEquipmentTO.setSeals(Collections.singletonList(sealTO));
    utilizedTransportEquipmentTO.setCargoItems(Collections.singletonList(cargoItemTO));
    utilizedTransportEquipmentTO.setActiveReeferSettings(activeReeferSettingsTO);
  }

  @Nested
  @DisplayName(
      "Tests for the method createUtilizedTransportEquipment(#shipmentID, #shippingInstructionReference, #utilizedTransportEquipments)")
  class testCreateUtilizedTransportEquipment {

    @Test
    @DisplayName("Test create UtilizedTransportEquipment")
    void testCreateUtilizedTransportEquipmentService() {
      UUID shipmentID = UUID.randomUUID();
      String shippingInstructionReference = "shippingInstructionReference1";
      UUID utilizedTransportEquipmentID = UUID.randomUUID();
      utilizedTransportEquipment.setShipmentID(shipmentID);
      utilizedTransportEquipment.setId(utilizedTransportEquipmentID);
      cargoItem.setShippingInstructionReference(shippingInstructionReference);

      when(utilizedTransportEquipmentRepository.save(any()))
          .thenReturn(Mono.just(utilizedTransportEquipment));
      when(equipmentRepository.save(eq(equipment))).thenReturn(Mono.just(equipment));
      when(sealRepository.save(any())).thenReturn(Mono.just(seal));
      when(activeReeferSettingsRepository.save(any())).thenReturn(Mono.just(activeReeferSettings));
      when(cargoItemRepository.save(any())).thenReturn(Mono.just(cargoItem));
      when(cargoLineItemRepository.save(any())).thenReturn(Mono.just(cargoLineItem));
      when(referenceService.createReferencesByShippingInstructionReferenceAndTOs(
              eq(shippingInstructionReference), any()))
          .thenReturn(Mono.just(List.of(referenceTO)));

      ArgumentCaptor<UtilizedTransportEquipment> argumentCaptorUtilizedTransportEquipment =
          ArgumentCaptor.forClass(UtilizedTransportEquipment.class);
      ArgumentCaptor<CargoItem> argumentCaptorCargoItem = ArgumentCaptor.forClass(CargoItem.class);
      ArgumentCaptor<ActiveReeferSettings> argumentCaptorActiveReeferSettings =
          ArgumentCaptor.forClass(ActiveReeferSettings.class);
      ArgumentCaptor<Seal> argumentCaptorSeal = ArgumentCaptor.forClass(Seal.class);
      StepVerifier.create(
              utilizedTransportEquipmentService.createUtilizedTransportEquipment(
                  shippingInstructionReference,
                  Collections.singletonList(utilizedTransportEquipmentTO)))
          .assertNext(
              utilizedTransportEquipmentTOS -> {
                verify(utilizedTransportEquipmentRepository)
                    .save(argumentCaptorUtilizedTransportEquipment.capture());
                verify(equipmentRepository).save(any());
                verify(sealRepository).save(argumentCaptorSeal.capture());
                verify(activeReeferSettingsRepository)
                    .save(argumentCaptorActiveReeferSettings.capture());
                verify(cargoItemRepository).save(argumentCaptorCargoItem.capture());
                verify(cargoLineItemRepository).save(any());
                verify(referenceService)
                    .createReferencesByShippingInstructionReferenceAndTOs(any(), any());
                assertEquals(
                    shipmentID,
                    argumentCaptorUtilizedTransportEquipment.getValue().getShipmentID());
                assertEquals(
                    shippingInstructionReference,
                    argumentCaptorCargoItem.getValue().getShippingInstructionReference());
                assertEquals(
                    utilizedTransportEquipmentID,
                    argumentCaptorActiveReeferSettings
                        .getValue()
                        .getUtilizedTransportEquipmentID());
                assertEquals(
                    utilizedTransportEquipmentID,
                    argumentCaptorSeal.getValue().getUtilizedTransportEquipmentID());
              })
          .verifyComplete();
    }

    @Test
    @DisplayName("Test create UtilizedTransportEquipment without ActiveReeferSettings")
    void testCreateUtilizedTransportEquipmentWithoutActiveReeferSettings() {
      UUID shipmentID = UUID.randomUUID();
      String shippingInstructionReference = "shippingInstructionReference1";
      UUID utilizedTransportEquipmentID = UUID.randomUUID();
      utilizedTransportEquipment.setShipmentID(shipmentID);
      utilizedTransportEquipment.setId(utilizedTransportEquipmentID);
      cargoItem.setShippingInstructionReference(shippingInstructionReference);
      utilizedTransportEquipmentTO.setActiveReeferSettings(null);

      when(utilizedTransportEquipmentRepository.save(any()))
          .thenReturn(Mono.just(utilizedTransportEquipment));
      when(equipmentRepository.save(eq(equipment))).thenReturn(Mono.just(equipment));
      when(sealRepository.save(any())).thenReturn(Mono.just(seal));
      when(cargoItemRepository.save(any())).thenReturn(Mono.just(cargoItem));
      when(cargoLineItemRepository.save(any())).thenReturn(Mono.just(cargoLineItem));
      when(referenceService.createReferencesByShippingInstructionReferenceAndTOs(
              eq(shippingInstructionReference), any()))
          .thenReturn(Mono.just(List.of(referenceTO)));

      ArgumentCaptor<UtilizedTransportEquipment> argumentCaptorUtilizedTransportEquipment =
          ArgumentCaptor.forClass(UtilizedTransportEquipment.class);
      ArgumentCaptor<CargoItem> argumentCaptorCargoItem = ArgumentCaptor.forClass(CargoItem.class);
      ArgumentCaptor<Seal> argumentCaptorSeal = ArgumentCaptor.forClass(Seal.class);
      StepVerifier.create(
              utilizedTransportEquipmentService.createUtilizedTransportEquipment(
                  shippingInstructionReference,
                  Collections.singletonList(utilizedTransportEquipmentTO)))
          .assertNext(
              utilizedTransportEquipmentTOS -> {
                verify(utilizedTransportEquipmentRepository)
                    .save(argumentCaptorUtilizedTransportEquipment.capture());
                verify(equipmentRepository).save(any());
                verify(sealRepository).save(argumentCaptorSeal.capture());
                verify(activeReeferSettingsRepository, never()).save(any());
                verify(cargoItemRepository).save(argumentCaptorCargoItem.capture());
                verify(cargoLineItemRepository).save(any());
                verify(referenceService)
                    .createReferencesByShippingInstructionReferenceAndTOs(any(), any());
                assertEquals(
                    shipmentID,
                    argumentCaptorUtilizedTransportEquipment.getValue().getShipmentID());
                assertEquals(
                    shippingInstructionReference,
                    argumentCaptorCargoItem.getValue().getShippingInstructionReference());
                assertEquals(
                    utilizedTransportEquipmentID,
                    argumentCaptorSeal.getValue().getUtilizedTransportEquipmentID());
              })
          .verifyComplete();
    }

    @Test
    @DisplayName("Test create UtilizedTransportEquipment without Seals")
    void testCreateUtilizedTransportEquipmentWithoutSeals() {
      UUID shipmentID = UUID.randomUUID();
      String shippingInstructionReference = "shippingInstructionReference1";
      UUID utilizedTransportEquipmentID = UUID.randomUUID();
      utilizedTransportEquipment.setShipmentID(shipmentID);
      utilizedTransportEquipment.setId(utilizedTransportEquipmentID);
      cargoItem.setShippingInstructionReference(shippingInstructionReference);
      utilizedTransportEquipmentTO.setSeals(null);

      when(utilizedTransportEquipmentRepository.save(any()))
          .thenReturn(Mono.just(utilizedTransportEquipment));
      when(equipmentRepository.save(eq(equipment))).thenReturn(Mono.just(equipment));
      when(activeReeferSettingsRepository.save(any())).thenReturn(Mono.just(activeReeferSettings));
      when(cargoItemRepository.save(any())).thenReturn(Mono.just(cargoItem));
      when(cargoLineItemRepository.save(any())).thenReturn(Mono.just(cargoLineItem));
      when(referenceService.createReferencesByShippingInstructionReferenceAndTOs(
              eq(shippingInstructionReference), any()))
          .thenReturn(Mono.just(List.of(referenceTO)));

      ArgumentCaptor<UtilizedTransportEquipment> argumentCaptorUtilizedTransportEquipment =
          ArgumentCaptor.forClass(UtilizedTransportEquipment.class);
      ArgumentCaptor<CargoItem> argumentCaptorCargoItem = ArgumentCaptor.forClass(CargoItem.class);
      ArgumentCaptor<ActiveReeferSettings> argumentCaptorActiveReeferSettings =
          ArgumentCaptor.forClass(ActiveReeferSettings.class);
      StepVerifier.create(
              utilizedTransportEquipmentService.createUtilizedTransportEquipment(
                  shippingInstructionReference,
                  Collections.singletonList(utilizedTransportEquipmentTO)))
          .assertNext(
              utilizedTransportEquipmentTOS -> {
                verify(utilizedTransportEquipmentRepository)
                    .save(argumentCaptorUtilizedTransportEquipment.capture());
                verify(equipmentRepository).save(any());
                verify(sealRepository, never()).save(any());
                verify(activeReeferSettingsRepository)
                    .save(argumentCaptorActiveReeferSettings.capture());
                verify(cargoItemRepository).save(argumentCaptorCargoItem.capture());
                verify(cargoLineItemRepository).save(any());
                verify(referenceService)
                    .createReferencesByShippingInstructionReferenceAndTOs(any(), any());
                assertEquals(
                    shipmentID,
                    argumentCaptorUtilizedTransportEquipment.getValue().getShipmentID());
                assertEquals(
                    shippingInstructionReference,
                    argumentCaptorCargoItem.getValue().getShippingInstructionReference());
                assertEquals(
                    utilizedTransportEquipmentID,
                    argumentCaptorActiveReeferSettings
                        .getValue()
                        .getUtilizedTransportEquipmentID());
              })
          .verifyComplete();
    }

    @Test
    @DisplayName("Test create utilizedTransportEquipment without CargoItems")
    void testCreateUtilizedTransportEquipmentWithoutCargoItems() {
      UUID shipmentID = UUID.randomUUID();
      String shippingInstructionReference = "shippingInstructionReference1";
      UUID utilizedTransportEquipmentID = UUID.randomUUID();
      utilizedTransportEquipment.setShipmentID(shipmentID);
      utilizedTransportEquipment.setId(utilizedTransportEquipmentID);
      utilizedTransportEquipmentTO.setCargoItems(null);

      when(utilizedTransportEquipmentRepository.save(any()))
          .thenReturn(Mono.just(utilizedTransportEquipment));
      when(equipmentRepository.save(eq(equipment))).thenReturn(Mono.just(equipment));
      when(sealRepository.save(any())).thenReturn(Mono.just(seal));
      when(activeReeferSettingsRepository.save(any())).thenReturn(Mono.just(activeReeferSettings));

      ArgumentCaptor<UtilizedTransportEquipment> argumentCaptorUtilizedTransportEquipment =
          ArgumentCaptor.forClass(UtilizedTransportEquipment.class);
      ArgumentCaptor<ActiveReeferSettings> argumentCaptorActiveReeferSettings =
          ArgumentCaptor.forClass(ActiveReeferSettings.class);
      ArgumentCaptor<Seal> argumentCaptorSeal = ArgumentCaptor.forClass(Seal.class);
      StepVerifier.create(
              utilizedTransportEquipmentService.createUtilizedTransportEquipment(
                  shippingInstructionReference,
                  Collections.singletonList(utilizedTransportEquipmentTO)))
          .assertNext(
              utilizedTransportEquipmentTOS -> {
                verify(utilizedTransportEquipmentRepository)
                    .save(argumentCaptorUtilizedTransportEquipment.capture());
                verify(equipmentRepository).save(any());
                verify(sealRepository).save(argumentCaptorSeal.capture());
                verify(activeReeferSettingsRepository)
                    .save(argumentCaptorActiveReeferSettings.capture());
                verify(cargoItemRepository, never()).save(any());
                verify(cargoLineItemRepository, never()).save(any());
                assertEquals(
                    shipmentID,
                    argumentCaptorUtilizedTransportEquipment.getValue().getShipmentID());
                assertEquals(
                    utilizedTransportEquipmentID,
                    argumentCaptorActiveReeferSettings
                        .getValue()
                        .getUtilizedTransportEquipmentID());
                assertEquals(
                    utilizedTransportEquipmentID,
                    argumentCaptorSeal.getValue().getUtilizedTransportEquipmentID());
              })
          .verifyComplete();
    }

    @Test
    @DisplayName("Test create utilizedTransportEquipment without CargoLineItems")
    void testCreateUtilizedTransportEquipmentWithoutCargoLineItems() {
      UUID shipmentID = UUID.randomUUID();
      String shippingInstructionReference = "shippingInstructionReference1";
      UUID utilizedTransportEquipmentID = UUID.randomUUID();
      utilizedTransportEquipment.setShipmentID(shipmentID);
      utilizedTransportEquipment.setId(utilizedTransportEquipmentID);
      cargoItem.setShippingInstructionReference(shippingInstructionReference);
      utilizedTransportEquipmentTO
          .getCargoItems()
          .forEach(cargoItemTO -> cargoItemTO.setCargoLineItems(null));

      when(utilizedTransportEquipmentRepository.save(any()))
          .thenReturn(Mono.just(utilizedTransportEquipment));
      when(equipmentRepository.save(eq(equipment))).thenReturn(Mono.just(equipment));
      when(sealRepository.save(any())).thenReturn(Mono.just(seal));
      when(activeReeferSettingsRepository.save(any())).thenReturn(Mono.just(activeReeferSettings));
      when(cargoItemRepository.save(any())).thenReturn(Mono.just(cargoItem));

      ArgumentCaptor<UtilizedTransportEquipment> argumentCaptorUtilizedTransportEquipment =
          ArgumentCaptor.forClass(UtilizedTransportEquipment.class);
      ArgumentCaptor<CargoItem> argumentCaptorCargoItem = ArgumentCaptor.forClass(CargoItem.class);
      ArgumentCaptor<ActiveReeferSettings> argumentCaptorActiveReeferSettings =
          ArgumentCaptor.forClass(ActiveReeferSettings.class);
      ArgumentCaptor<Seal> argumentCaptorSeal = ArgumentCaptor.forClass(Seal.class);
      StepVerifier.create(
              utilizedTransportEquipmentService.createUtilizedTransportEquipment(
                  shippingInstructionReference,
                  Collections.singletonList(utilizedTransportEquipmentTO)))
          .assertNext(
              utilizedTransportEquipmentTOS -> {
                verify(utilizedTransportEquipmentRepository)
                    .save(argumentCaptorUtilizedTransportEquipment.capture());
                verify(equipmentRepository).save(any());
                verify(sealRepository).save(argumentCaptorSeal.capture());
                verify(activeReeferSettingsRepository)
                    .save(argumentCaptorActiveReeferSettings.capture());
                verify(cargoItemRepository).save(argumentCaptorCargoItem.capture());
                verify(cargoLineItemRepository, never()).save(any());
                assertEquals(
                    shipmentID,
                    argumentCaptorUtilizedTransportEquipment.getValue().getShipmentID());
                assertEquals(
                    utilizedTransportEquipmentID,
                    argumentCaptorActiveReeferSettings
                        .getValue()
                        .getUtilizedTransportEquipmentID());
                assertEquals(
                    shippingInstructionReference,
                    argumentCaptorCargoItem.getValue().getShippingInstructionReference());
                assertEquals(
                    utilizedTransportEquipmentID,
                    argumentCaptorSeal.getValue().getUtilizedTransportEquipmentID());
              })
          .verifyComplete();
    }

    @Test
    @DisplayName("Test create UtilizedTransportEquipment with multiple seals")
    void testCreateUtilizedTransportEquipmentServiceWithMultipleSeals() {
      UUID shipmentID = UUID.randomUUID();
      String shippingInstructionReference = "shippingInstructionReference1";
      UUID utilizedTransportEquipmentID = UUID.randomUUID();
      utilizedTransportEquipment.setShipmentID(shipmentID);
      utilizedTransportEquipment.setId(utilizedTransportEquipmentID);
      cargoItem.setShippingInstructionReference(shippingInstructionReference);

      SealTO sealTO = utilizedTransportEquipmentTO.getSeals().get(0);
      SealTO additionalSealTO = utilizedTransportEquipmentTO.getSeals().get(0);
      List<SealTO> sealTOList = List.of(sealTO, additionalSealTO);
      utilizedTransportEquipmentTO.setSeals(sealTOList);

      when(utilizedTransportEquipmentRepository.save(any()))
          .thenReturn(Mono.just(utilizedTransportEquipment));
      when(equipmentRepository.save(eq(equipment))).thenReturn(Mono.just(equipment));
      when(sealRepository.save(any())).thenReturn(Mono.just(seal));
      when(activeReeferSettingsRepository.save(any())).thenReturn(Mono.just(activeReeferSettings));
      when(cargoItemRepository.save(any())).thenReturn(Mono.just(cargoItem));
      when(cargoLineItemRepository.save(any())).thenReturn(Mono.just(cargoLineItem));
      when(referenceService.createReferencesByShippingInstructionReferenceAndTOs(
              eq(shippingInstructionReference), any()))
          .thenReturn(Mono.just(List.of(referenceTO)));

      ArgumentCaptor<UtilizedTransportEquipment> argumentCaptorUtilizedTransportEquipment =
          ArgumentCaptor.forClass(UtilizedTransportEquipment.class);
      ArgumentCaptor<CargoItem> argumentCaptorCargoItem = ArgumentCaptor.forClass(CargoItem.class);
      ArgumentCaptor<ActiveReeferSettings> argumentCaptorActiveReeferSettings =
          ArgumentCaptor.forClass(ActiveReeferSettings.class);
      ArgumentCaptor<Seal> argumentCaptorSeal = ArgumentCaptor.forClass(Seal.class);
      StepVerifier.create(
              utilizedTransportEquipmentService.createUtilizedTransportEquipment(
                  shippingInstructionReference,
                  Collections.singletonList(utilizedTransportEquipmentTO)))
          .assertNext(
              utilizedTransportEquipmentTOS -> {
                verify(utilizedTransportEquipmentRepository)
                    .save(argumentCaptorUtilizedTransportEquipment.capture());
                verify(equipmentRepository).save(any());
                verify(sealRepository, times(2)).save(argumentCaptorSeal.capture());
                verify(activeReeferSettingsRepository)
                    .save(argumentCaptorActiveReeferSettings.capture());
                verify(cargoItemRepository).save(argumentCaptorCargoItem.capture());
                verify(cargoLineItemRepository).save(any());
                verify(referenceService)
                    .createReferencesByShippingInstructionReferenceAndTOs(any(), any());
                assertEquals(
                    shipmentID,
                    argumentCaptorUtilizedTransportEquipment.getValue().getShipmentID());
                assertEquals(
                    shippingInstructionReference,
                    argumentCaptorCargoItem.getValue().getShippingInstructionReference());
                assertEquals(
                    utilizedTransportEquipmentID,
                    argumentCaptorActiveReeferSettings
                        .getValue()
                        .getUtilizedTransportEquipmentID());
                assertEquals(
                    utilizedTransportEquipmentID,
                    argumentCaptorSeal.getValue().getUtilizedTransportEquipmentID());
              })
          .verifyComplete();
    }

    @Test
    @DisplayName(
        "Test create UtilizedTransportEquipment with multiple cargoItems and multiple cargoLineItems")
    void testCreateUtilizedTransportEquipmentServiceWithCargoItems() {
      UUID shipmentID = UUID.randomUUID();
      String shippingInstructionReference = "shippingInstructionReference1";
      UUID utilizedTransportEquipmentID = UUID.randomUUID();
      utilizedTransportEquipment.setShipmentID(shipmentID);
      utilizedTransportEquipment.setId(utilizedTransportEquipmentID);
      cargoItem.setShippingInstructionReference(shippingInstructionReference);

      CargoLineItemTO cargoLineItemTO1 = new CargoLineItemTO();
      cargoLineItemTO1.setCargoLineItemID("1");
      cargoLineItemTO1.setShippingMarks("mark1");

      CargoLineItemTO cargoLineItemTO2 = new CargoLineItemTO();
      cargoLineItemTO2.setCargoLineItemID("1");
      cargoLineItemTO2.setShippingMarks("mark1");

      CargoItemTO cargoItemTO1 = new CargoItemTO();
      cargoItemTO1.setWeight(10F);
      cargoItemTO1.setWeightUnit(WeightUnit.KGM);
      cargoItemTO1.setNumberOfPackages(1);
      cargoItemTO1.setPackageCode("abc");
      cargoItemTO1.setCargoLineItems(List.of(cargoLineItemTO1, cargoLineItemTO2));

      CargoItemTO cargoItemTO2 = new CargoItemTO();
      cargoItemTO2.setWeight(10F);
      cargoItemTO2.setWeightUnit(WeightUnit.KGM);
      cargoItemTO2.setNumberOfPackages(1);
      cargoItemTO2.setPackageCode("def");
      cargoItemTO2.setCargoLineItems(List.of(cargoLineItemTO1));

      utilizedTransportEquipmentTO.setCargoItems(List.of(cargoItemTO1, cargoItemTO2));

      when(utilizedTransportEquipmentRepository.save(any()))
          .thenReturn(Mono.just(utilizedTransportEquipment));
      when(equipmentRepository.save(eq(equipment))).thenReturn(Mono.just(equipment));
      when(sealRepository.save(any())).thenReturn(Mono.just(seal));
      when(activeReeferSettingsRepository.save(any())).thenReturn(Mono.just(activeReeferSettings));
      when(cargoItemRepository.save(any())).thenReturn(Mono.just(cargoItem));
      when(cargoLineItemRepository.save(any())).thenReturn(Mono.just(cargoLineItem));
      when(referenceService.createReferencesByShippingInstructionReferenceAndTOs(
              eq(shippingInstructionReference), any()))
          .thenReturn(Mono.just(List.of(referenceTO)));

      ArgumentCaptor<UtilizedTransportEquipment> argumentCaptorUtilizedTransportEquipment =
          ArgumentCaptor.forClass(UtilizedTransportEquipment.class);
      ArgumentCaptor<CargoItem> argumentCaptorCargoItem = ArgumentCaptor.forClass(CargoItem.class);
      ArgumentCaptor<ActiveReeferSettings> argumentCaptorActiveReeferSettings =
          ArgumentCaptor.forClass(ActiveReeferSettings.class);
      ArgumentCaptor<Seal> argumentCaptorSeal = ArgumentCaptor.forClass(Seal.class);
      StepVerifier.create(
              utilizedTransportEquipmentService.createUtilizedTransportEquipment(
                  shippingInstructionReference,
                  Collections.singletonList(utilizedTransportEquipmentTO)))
          .assertNext(
              utilizedTransportEquipmentTOS -> {
                verify(utilizedTransportEquipmentRepository)
                    .save(argumentCaptorUtilizedTransportEquipment.capture());
                verify(equipmentRepository).save(any());
                verify(sealRepository).save(argumentCaptorSeal.capture());
                verify(activeReeferSettingsRepository)
                    .save(argumentCaptorActiveReeferSettings.capture());
                verify(cargoItemRepository, times(2)).save(argumentCaptorCargoItem.capture());
                verify(cargoLineItemRepository, times(3)).save(any());
                verify(referenceService, times(2))
                    .createReferencesByShippingInstructionReferenceAndTOs(any(), any());
                assertEquals(
                    shipmentID,
                    argumentCaptorUtilizedTransportEquipment.getValue().getShipmentID());
                assertEquals(
                    shippingInstructionReference,
                    argumentCaptorCargoItem.getValue().getShippingInstructionReference());
                assertEquals(
                    utilizedTransportEquipmentID,
                    argumentCaptorActiveReeferSettings
                        .getValue()
                        .getUtilizedTransportEquipmentID());
                assertEquals(
                    utilizedTransportEquipmentID,
                    argumentCaptorSeal.getValue().getUtilizedTransportEquipmentID());
              })
          .verifyComplete();
    }

    @Test
    @DisplayName(
        "Test getCarrierBookingReferences with carrierBookingReference on ShippingInstruction")
    void testGetCarrierBookingReferencesViaShippingInstruction() {
      ShippingInstructionTO shippingInstructionTO = new ShippingInstructionTO();
      shippingInstructionTO.setCarrierBookingReference("CBR");
      List<String> carrierBookingReferences =
          utilizedTransportEquipmentService.getCarrierBookingReferences(shippingInstructionTO);
      assertEquals(1, carrierBookingReferences.size());
      assertEquals("CBR", carrierBookingReferences.get(0));
    }

    @Test
    @DisplayName(
        "Test getCarrierBookingReferences with carrierBookingReference on UtilizedTransportEquipment")
    void testGetCarrierBookingReferencesViaUtilizedTransportEquipment() {
      ShippingInstructionTO shippingInstructionTO = new ShippingInstructionTO();
      UtilizedTransportEquipmentTO utilizedTransportEquipmentTO =
          new UtilizedTransportEquipmentTO();
      utilizedTransportEquipmentTO.setCarrierBookingReference("CBR");
      shippingInstructionTO.setUtilizedTransportEquipments(List.of(utilizedTransportEquipmentTO));
      List<String> carrierBookingReferences =
          utilizedTransportEquipmentService.getCarrierBookingReferences(shippingInstructionTO);
      assertEquals(1, carrierBookingReferences.size());
      assertEquals("CBR", carrierBookingReferences.get(0));
    }

    @Test
    @DisplayName(
        "Test getCarrierBookingReferences with multiple carrierBookingReferences on UtilizedTransportEquipment")
    void testGetMultipleCarrierBookingReferencesViaUtilizedTransportEquipment() {
      ShippingInstructionTO shippingInstructionTO = new ShippingInstructionTO();
      UtilizedTransportEquipmentTO utilizedTransportEquipmentTO1 =
          new UtilizedTransportEquipmentTO();
      utilizedTransportEquipmentTO1.setCarrierBookingReference("CBR1");
      UtilizedTransportEquipmentTO utilizedTransportEquipmentTO2 =
          new UtilizedTransportEquipmentTO();
      utilizedTransportEquipmentTO2.setCarrierBookingReference("CBR2");
      shippingInstructionTO.setUtilizedTransportEquipments(
          List.of(utilizedTransportEquipmentTO1, utilizedTransportEquipmentTO2));
      List<String> carrierBookingReferences =
          utilizedTransportEquipmentService.getCarrierBookingReferences(shippingInstructionTO);
      assertEquals(2, carrierBookingReferences.size());
      assertEquals("CBR1", carrierBookingReferences.get(0));
      assertEquals("CBR2", carrierBookingReferences.get(1));
    }

    @Test
    @DisplayName("Test getCarrierBookingReferences with no CarrierBookingReference set")
    void testGetCarrierBookingReferencesNotSet() {
      ShippingInstructionTO shippingInstructionTO = new ShippingInstructionTO();
      UtilizedTransportEquipmentTO utilizedTransportEquipmentTO =
          new UtilizedTransportEquipmentTO();
      shippingInstructionTO.setUtilizedTransportEquipments(List.of(utilizedTransportEquipmentTO));
      List<String> carrierBookingReferences =
          utilizedTransportEquipmentService.getCarrierBookingReferences(shippingInstructionTO);
      assertEquals(0, carrierBookingReferences.size());
    }

    @Test
    @DisplayName("Test addUtilizedTransportEquipmentToShippingInstruction")
    void testAddUtilizedTransportEquipmentToShippingInstruction() {
      UUID shipmentID = UUID.randomUUID();
      String shippingInstructionReference = "shippingInstructionReference1";
      UUID utilizedTransportEquipmentID = UUID.randomUUID();
      utilizedTransportEquipment.setShipmentID(shipmentID);
      utilizedTransportEquipment.setId(utilizedTransportEquipmentID);
      cargoItem.setShippingInstructionReference(shippingInstructionReference);

      when(shipmentRepository.findByCarrierBookingReference(any())).thenReturn(Mono.just(shipment));

      when(utilizedTransportEquipmentRepository.save(any()))
          .thenReturn(Mono.just(utilizedTransportEquipment));
      when(equipmentRepository.save(eq(equipment))).thenReturn(Mono.just(equipment));
      when(sealRepository.save(any())).thenReturn(Mono.just(seal));
      when(activeReeferSettingsRepository.save(any())).thenReturn(Mono.just(activeReeferSettings));
      when(cargoItemRepository.save(any())).thenReturn(Mono.just(cargoItem));
      when(cargoLineItemRepository.save(any())).thenReturn(Mono.just(cargoLineItem));
      when(referenceService.createReferencesByShippingInstructionReferenceAndTOs(
              eq(shippingInstructionReference), any()))
          .thenReturn(Mono.just(List.of(referenceTO)));

      ShippingInstructionTO shippingInstructionTO = new ShippingInstructionTO();
      shippingInstructionTO.setShippingInstructionReference(shippingInstructionReference);
      shippingInstructionTO.setUtilizedTransportEquipments(List.of(utilizedTransportEquipmentTO));

      StepVerifier.create(
              utilizedTransportEquipmentService.addUtilizedTransportEquipmentToShippingInstruction(
                  List.of(utilizedTransportEquipmentTO), shippingInstructionTO))
          .assertNext(
              utilizedTransportEquipmentTOS -> {
                assertEquals(
                    shippingInstructionReference,
                    shippingInstructionTO.getShippingInstructionReference());
                assertEquals(
                    "carrierBookingReference1",
                    utilizedTransportEquipmentTOS.get(0).getCarrierBookingReference());
              })
          .verifyComplete();
    }
  }

  @Nested
  @DisplayName("Tests for the method findUtilizedTransportEquipmentByShipmentID(#shipmentID)")
  class testFindUtilizedTransportEquipment {

    UtilizedTransportEquipmentCustomRepository.UtilizedTransportEquipmentDetails
        utilizedTransportEquipmentDetails;
    CargoItemCustomRepository.CargoItemWithCargoLineItems cargoItemWithCargoLineItems;
    CargoItemCustomRepository.CargoItemWithCargoLineItems cargoItemWithCargoLineItems2;

    @BeforeEach
    void init() {
      utilizedTransportEquipmentDetails =
          new UtilizedTransportEquipmentCustomRepository.UtilizedTransportEquipmentDetails(
              "CBR1",
              equipment.getEquipmentReference(),
              utilizedTransportEquipment.getCargoGrossWeight(),
              utilizedTransportEquipment.getCargoGrossWeightUnit(),
              equipment.getIsoEquipmentCode(),
              equipment.getTareWeight(),
              equipment.getWeightUnit(),
              utilizedTransportEquipment.getIsShipperOwned(),
              utilizedTransportEquipment.getId());

      cargoLineItem = new CargoLineItem();
      cargoLineItem.setCargoItemID(cargoItem.getId());
      cargoLineItem.setCargoLineItemID("CargoLineItem");
      cargoLineItem.setShippingMarks("shippingMarks");

      cargoItemWithCargoLineItems = new CargoItemCustomRepository.CargoItemWithCargoLineItems();
      cargoItemWithCargoLineItems.setCargoLineItems(List.of(cargoLineItem));
      cargoItemWithCargoLineItems.setUtilizedTransportEquipmentID(
          utilizedTransportEquipment.getId());
      cargoItemWithCargoLineItems.setPackageCode(cargoItem.getPackageCode());
      cargoItemWithCargoLineItems.setVolume(cargoItem.getVolume());
      cargoItemWithCargoLineItems.setVolumeUnit(cargoItem.getVolumeUnit());
      cargoItemWithCargoLineItems.setShippingInstructionReference(
          cargoItem.getShippingInstructionReference());
      cargoItemWithCargoLineItems.setPackageCode(cargoItem.getPackageCode());
      cargoItemWithCargoLineItems.setNumberOfPackages(2);
      cargoItemWithCargoLineItems.setWeight(cargoItem.getWeight());
      cargoItemWithCargoLineItems.setWeightUnit(cargoItem.getWeightUnit());
      cargoItemWithCargoLineItems.setId(cargoItem.getId());

      cargoItemWithCargoLineItems2 = new CargoItemCustomRepository.CargoItemWithCargoLineItems();
      cargoItemWithCargoLineItems2.setUtilizedTransportEquipmentID(
          utilizedTransportEquipment.getId());
      cargoItemWithCargoLineItems2.setPackageCode(cargoItem.getPackageCode());
      cargoItemWithCargoLineItems2.setVolume(cargoItem.getVolume());
      cargoItemWithCargoLineItems2.setVolumeUnit(cargoItem.getVolumeUnit());
      cargoItemWithCargoLineItems2.setShippingInstructionReference(
          cargoItem.getShippingInstructionReference());
      cargoItemWithCargoLineItems2.setPackageCode(cargoItem.getPackageCode());
      cargoItemWithCargoLineItems2.setNumberOfPackages(2);
      cargoItemWithCargoLineItems2.setWeight(cargoItem.getWeight());
      cargoItemWithCargoLineItems2.setWeightUnit(cargoItem.getWeightUnit());
      cargoItemWithCargoLineItems2.setId(UUID.randomUUID());
    }

    @Test
    @DisplayName(
        "Test find UtilizedTransportEquipment with all underlying objects should return full utilizedTransportEquipment")
    void testFindUtilizedTransportEquipment() {
      UUID shipmentID = UUID.randomUUID();

      when(utilizedTransportEquipmentRepository.findUtilizedTransportEquipmentDetailsByShipmentID(
              eq(shipmentID)))
          .thenReturn(Flux.just(utilizedTransportEquipmentDetails));
      when(cargoItemRepository.findAllCargoItemsAndCargoLineItemsByUtilizedTransportEquipmentID(
              eq(utilizedTransportEquipment.getId())))
          .thenReturn(Flux.just(cargoItemWithCargoLineItems));
      when(referenceService.findByCargoItemID(eq(cargoItem.getId())))
          .thenReturn(Mono.just(List.of(referenceTO)));
      when(sealRepository.findAllByUtilizedTransportEquipmentID(
              eq(utilizedTransportEquipment.getId())))
          .thenReturn(Flux.just(seal));
      when(activeReeferSettingsRepository.findById(eq(utilizedTransportEquipment.getId())))
          .thenReturn(Mono.just(activeReeferSettings));

      StepVerifier.create(
              utilizedTransportEquipmentService.findUtilizedTransportEquipmentByShipmentID(
                  shipmentID))
          .assertNext(
              utilizedTransportEquipmentTO -> {
                assertEquals(1, utilizedTransportEquipmentTO.size());
                assertEquals(1, utilizedTransportEquipmentTO.get(0).getCargoItems().size());
                assertEquals(
                    1,
                    utilizedTransportEquipmentTO
                        .get(0)
                        .getCargoItems()
                        .get(0)
                        .getCargoLineItems()
                        .size());
                assertEquals(1, utilizedTransportEquipmentTO.get(0).getSeals().size());
                assertEquals(
                    1,
                    utilizedTransportEquipmentTO
                        .get(0)
                        .getCargoItems()
                        .get(0)
                        .getReferences()
                        .size());
                assertEquals(
                    "CBR1", utilizedTransportEquipmentTO.get(0).getCarrierBookingReference());
                assertEquals(
                    utilizedTransportEquipment.getEquipmentReference(),
                    utilizedTransportEquipmentTO.get(0).getEquipment().getEquipmentReference());
                assertEquals(
                    activeReeferSettings.getHumidityMax(),
                    utilizedTransportEquipmentTO.get(0).getActiveReeferSettings().getHumidityMax());
                assertEquals(
                    seal.getSealSource(),
                    utilizedTransportEquipmentTO.get(0).getSeals().get(0).getSealSource().name());
                assertEquals(
                    referenceTO.getReferenceValue(),
                    utilizedTransportEquipmentTO
                        .get(0)
                        .getCargoItems()
                        .get(0)
                        .getReferences()
                        .get(0)
                        .getReferenceValue());
                assertEquals(
                    cargoLineItem.getShippingMarks(),
                    utilizedTransportEquipmentTO
                        .get(0)
                        .getCargoItems()
                        .get(0)
                        .getCargoLineItems()
                        .get(0)
                        .getShippingMarks());
              })
          .verifyComplete();
    }

    @Test
    @DisplayName(
        "Test find UtilizedTransportEquipment without active Reefersettings should return utilizedTransportEquipment without active reefer settings")
    void testFindUtilizedTransportEquipmentWithoutActiveReeferSettings() {
      UUID shipmentID = UUID.randomUUID();

      when(utilizedTransportEquipmentRepository.findUtilizedTransportEquipmentDetailsByShipmentID(
              eq(shipmentID)))
          .thenReturn(Flux.just(utilizedTransportEquipmentDetails));
      when(cargoItemRepository.findAllCargoItemsAndCargoLineItemsByUtilizedTransportEquipmentID(
              eq(utilizedTransportEquipment.getId())))
          .thenReturn(Flux.just(cargoItemWithCargoLineItems));
      when(referenceService.findByCargoItemID(eq(cargoItem.getId())))
          .thenReturn(Mono.just(List.of(referenceTO)));
      when(sealRepository.findAllByUtilizedTransportEquipmentID(
              eq(utilizedTransportEquipment.getId())))
          .thenReturn(Flux.just(seal));
      when(activeReeferSettingsRepository.findById(eq(utilizedTransportEquipment.getId())))
          .thenReturn(Mono.empty());

      StepVerifier.create(
              utilizedTransportEquipmentService.findUtilizedTransportEquipmentByShipmentID(
                  shipmentID))
          .assertNext(
              utilizedTransportEquipmentTO -> {
                assertEquals(1, utilizedTransportEquipmentTO.size());
                assertEquals(1, utilizedTransportEquipmentTO.get(0).getCargoItems().size());
                assertEquals(
                    1,
                    utilizedTransportEquipmentTO
                        .get(0)
                        .getCargoItems()
                        .get(0)
                        .getCargoLineItems()
                        .size());
                assertEquals(1, utilizedTransportEquipmentTO.get(0).getSeals().size());
                assertEquals(
                    1,
                    utilizedTransportEquipmentTO
                        .get(0)
                        .getCargoItems()
                        .get(0)
                        .getReferences()
                        .size());
                assertEquals(
                    "CBR1", utilizedTransportEquipmentTO.get(0).getCarrierBookingReference());
                assertEquals(
                    utilizedTransportEquipment.getEquipmentReference(),
                    utilizedTransportEquipmentTO.get(0).getEquipment().getEquipmentReference());
                assertNull(utilizedTransportEquipmentTO.get(0).getActiveReeferSettings());
                assertEquals(
                    seal.getSealSource(),
                    utilizedTransportEquipmentTO.get(0).getSeals().get(0).getSealSource().name());
                assertEquals(
                    referenceTO.getReferenceValue(),
                    utilizedTransportEquipmentTO
                        .get(0)
                        .getCargoItems()
                        .get(0)
                        .getReferences()
                        .get(0)
                        .getReferenceValue());
                assertEquals(
                    cargoLineItem.getShippingMarks(),
                    utilizedTransportEquipmentTO
                        .get(0)
                        .getCargoItems()
                        .get(0)
                        .getCargoLineItems()
                        .get(0)
                        .getShippingMarks());
              })
          .verifyComplete();
    }

    @Test
    @DisplayName(
        "Test find UtilizedTransportEquipment without Seals should return utilizedTransportEquipment without seals")
    void testFindUtilizedTransportEquipmentWithoutSeals() {
      UUID shipmentID = UUID.randomUUID();

      when(utilizedTransportEquipmentRepository.findUtilizedTransportEquipmentDetailsByShipmentID(
              eq(shipmentID)))
          .thenReturn(Flux.just(utilizedTransportEquipmentDetails));
      when(cargoItemRepository.findAllCargoItemsAndCargoLineItemsByUtilizedTransportEquipmentID(
              eq(utilizedTransportEquipment.getId())))
          .thenReturn(Flux.just(cargoItemWithCargoLineItems));
      when(referenceService.findByCargoItemID(eq(cargoItem.getId())))
          .thenReturn(Mono.just(List.of(referenceTO)));
      when(sealRepository.findAllByUtilizedTransportEquipmentID(
              eq(utilizedTransportEquipment.getId())))
          .thenReturn(Flux.empty());
      when(activeReeferSettingsRepository.findById(eq(utilizedTransportEquipment.getId())))
          .thenReturn(Mono.just(activeReeferSettings));

      StepVerifier.create(
              utilizedTransportEquipmentService.findUtilizedTransportEquipmentByShipmentID(
                  shipmentID))
          .assertNext(
              utilizedTransportEquipmentTO -> {
                assertEquals(1, utilizedTransportEquipmentTO.size());
                assertEquals(1, utilizedTransportEquipmentTO.get(0).getCargoItems().size());
                assertTrue(utilizedTransportEquipmentTO.get(0).getSeals().isEmpty());
                assertEquals(
                    1,
                    utilizedTransportEquipmentTO
                        .get(0)
                        .getCargoItems()
                        .get(0)
                        .getReferences()
                        .size());
                assertEquals(
                    "CBR1", utilizedTransportEquipmentTO.get(0).getCarrierBookingReference());
                assertEquals(
                    utilizedTransportEquipment.getEquipmentReference(),
                    utilizedTransportEquipmentTO.get(0).getEquipment().getEquipmentReference());
                assertEquals(
                    activeReeferSettings.getHumidityMax(),
                    utilizedTransportEquipmentTO.get(0).getActiveReeferSettings().getHumidityMax());
                assertEquals(
                    referenceTO.getReferenceValue(),
                    utilizedTransportEquipmentTO
                        .get(0)
                        .getCargoItems()
                        .get(0)
                        .getReferences()
                        .get(0)
                        .getReferenceValue());
                assertEquals(
                    cargoLineItem.getShippingMarks(),
                    utilizedTransportEquipmentTO
                        .get(0)
                        .getCargoItems()
                        .get(0)
                        .getCargoLineItems()
                        .get(0)
                        .getShippingMarks());
              })
          .verifyComplete();
    }

    @Test
    @DisplayName(
        "Test find UtilizedTransportEquipment without CargoItems underlying objects should return utilizedTransportEquipment without Cargoitems")
    void testFindUtilizedTransportEquipmentthoutCargoItems() {
      UUID shipmentID = UUID.randomUUID();

      when(utilizedTransportEquipmentRepository.findUtilizedTransportEquipmentDetailsByShipmentID(
              eq(shipmentID)))
          .thenReturn(Flux.just(utilizedTransportEquipmentDetails));
      when(cargoItemRepository.findAllCargoItemsAndCargoLineItemsByUtilizedTransportEquipmentID(
              eq(utilizedTransportEquipment.getId())))
          .thenReturn(Flux.empty());
      verify(referenceService, never()).findByCargoItemID(any());
      when(sealRepository.findAllByUtilizedTransportEquipmentID(
              eq(utilizedTransportEquipment.getId())))
          .thenReturn(Flux.just(seal));
      when(activeReeferSettingsRepository.findById(eq(utilizedTransportEquipment.getId())))
          .thenReturn(Mono.just(activeReeferSettings));

      StepVerifier.create(
              utilizedTransportEquipmentService.findUtilizedTransportEquipmentByShipmentID(
                  shipmentID))
          .assertNext(
              utilizedTransportEquipmentTO -> {
                assertEquals(1, utilizedTransportEquipmentTO.size());
                assertTrue(utilizedTransportEquipmentTO.get(0).getCargoItems().isEmpty());
                assertEquals(1, utilizedTransportEquipmentTO.get(0).getSeals().size());
                assertEquals(
                    utilizedTransportEquipment.getEquipmentReference(),
                    utilizedTransportEquipmentTO.get(0).getEquipment().getEquipmentReference());
                assertEquals(
                    activeReeferSettings.getHumidityMax(),
                    utilizedTransportEquipmentTO.get(0).getActiveReeferSettings().getHumidityMax());
                assertEquals(
                    seal.getSealSource(),
                    utilizedTransportEquipmentTO.get(0).getSeals().get(0).getSealSource().name());
              })
          .verifyComplete();
    }

    @Test
    @DisplayName(
        "Test find UtilizedTransportEquipment with multiple results should return all utilizedTransportEquipment")
    void testFindUtilizedTransportEquipmentMultipleResults() {
      UUID shipmentID = UUID.randomUUID();

      when(utilizedTransportEquipmentRepository.findUtilizedTransportEquipmentDetailsByShipmentID(
              eq(shipmentID)))
          .thenReturn(
              Flux.just(utilizedTransportEquipmentDetails, utilizedTransportEquipmentDetails));
      when(cargoItemRepository.findAllCargoItemsAndCargoLineItemsByUtilizedTransportEquipmentID(
              eq(utilizedTransportEquipment.getId())))
          .thenReturn(Flux.just(cargoItemWithCargoLineItems));
      when(referenceService.findByCargoItemID(eq(cargoItem.getId())))
          .thenReturn(Mono.just(List.of(referenceTO)));
      when(sealRepository.findAllByUtilizedTransportEquipmentID(
              eq(utilizedTransportEquipment.getId())))
          .thenReturn(Flux.just(seal));
      when(activeReeferSettingsRepository.findById(eq(utilizedTransportEquipment.getId())))
          .thenReturn(Mono.just(activeReeferSettings));

      StepVerifier.create(
              utilizedTransportEquipmentService.findUtilizedTransportEquipmentByShipmentID(
                  shipmentID))
          .assertNext(
              utilizedTransportEquipmentTO -> {
                assertEquals(2, utilizedTransportEquipmentTO.size());
                assertEquals(1, utilizedTransportEquipmentTO.get(0).getCargoItems().size());
                assertEquals(
                    1,
                    utilizedTransportEquipmentTO
                        .get(0)
                        .getCargoItems()
                        .get(0)
                        .getCargoLineItems()
                        .size());
                assertEquals(1, utilizedTransportEquipmentTO.get(0).getSeals().size());
                assertEquals(
                    1,
                    utilizedTransportEquipmentTO
                        .get(0)
                        .getCargoItems()
                        .get(0)
                        .getReferences()
                        .size());
                assertEquals(
                    "CBR1", utilizedTransportEquipmentTO.get(0).getCarrierBookingReference());
                assertEquals(
                    utilizedTransportEquipment.getEquipmentReference(),
                    utilizedTransportEquipmentTO.get(0).getEquipment().getEquipmentReference());
                assertEquals(
                    activeReeferSettings.getHumidityMax(),
                    utilizedTransportEquipmentTO.get(0).getActiveReeferSettings().getHumidityMax());
                assertEquals(
                    seal.getSealSource(),
                    utilizedTransportEquipmentTO.get(0).getSeals().get(0).getSealSource().name());
                assertEquals(
                    referenceTO.getReferenceValue(),
                    utilizedTransportEquipmentTO
                        .get(0)
                        .getCargoItems()
                        .get(0)
                        .getReferences()
                        .get(0)
                        .getReferenceValue());
                assertEquals(
                    cargoLineItem.getShippingMarks(),
                    utilizedTransportEquipmentTO
                        .get(0)
                        .getCargoItems()
                        .get(0)
                        .getCargoLineItems()
                        .get(0)
                        .getShippingMarks());
              })
          .verifyComplete();
    }

    @Test
    @DisplayName(
        "Test find UtilizedTransportEquipment with multiple CargoItems should return utilizedTransportEquipment with multiple cargoItems")
    void testFindUtilizedTransportEquipmentWithMultipleCargoItems() {
      UUID shipmentID = UUID.randomUUID();

      when(utilizedTransportEquipmentRepository.findUtilizedTransportEquipmentDetailsByShipmentID(
              eq(shipmentID)))
          .thenReturn(Flux.just(utilizedTransportEquipmentDetails));
      when(cargoItemRepository.findAllCargoItemsAndCargoLineItemsByUtilizedTransportEquipmentID(
              eq(utilizedTransportEquipment.getId())))
          .thenReturn(Flux.just(cargoItemWithCargoLineItems, cargoItemWithCargoLineItems2));
      when(referenceService.findByCargoItemID(eq(cargoItem.getId())))
          .thenReturn(Mono.just(List.of(referenceTO)));
      when(referenceService.findByCargoItemID(eq(cargoItemWithCargoLineItems2.getId())))
          .thenReturn(Mono.empty());
      when(sealRepository.findAllByUtilizedTransportEquipmentID(
              eq(utilizedTransportEquipment.getId())))
          .thenReturn(Flux.just(seal));
      when(activeReeferSettingsRepository.findById(eq(utilizedTransportEquipment.getId())))
          .thenReturn(Mono.just(activeReeferSettings));

      StepVerifier.create(
              utilizedTransportEquipmentService.findUtilizedTransportEquipmentByShipmentID(
                  shipmentID))
          .assertNext(
              utilizedTransportEquipmentTOS -> {
                assertEquals(1, utilizedTransportEquipmentTOS.size());
                assertEquals(2, utilizedTransportEquipmentTOS.get(0).getCargoItems().size());
                assertEquals(
                    1,
                    utilizedTransportEquipmentTOS
                        .get(0)
                        .getCargoItems()
                        .get(0)
                        .getCargoLineItems()
                        .size());
                assertEquals(1, utilizedTransportEquipmentTOS.get(0).getSeals().size());
                assertEquals(
                    1,
                    utilizedTransportEquipmentTOS
                        .get(0)
                        .getCargoItems()
                        .get(0)
                        .getReferences()
                        .size());
                assertNull(
                    utilizedTransportEquipmentTOS.get(0).getCargoItems().get(1).getReferences());
                assertEquals(
                    "CBR1", utilizedTransportEquipmentTOS.get(0).getCarrierBookingReference());
                assertEquals(
                    utilizedTransportEquipment.getEquipmentReference(),
                    utilizedTransportEquipmentTOS.get(0).getEquipment().getEquipmentReference());
                assertEquals(
                    activeReeferSettings.getHumidityMax(),
                    utilizedTransportEquipmentTOS
                        .get(0)
                        .getActiveReeferSettings()
                        .getHumidityMax());
                assertEquals(
                    seal.getSealSource(),
                    utilizedTransportEquipmentTOS.get(0).getSeals().get(0).getSealSource().name());
                assertEquals(
                    referenceTO.getReferenceValue(),
                    utilizedTransportEquipmentTOS
                        .get(0)
                        .getCargoItems()
                        .get(0)
                        .getReferences()
                        .get(0)
                        .getReferenceValue());
                assertEquals(
                    cargoLineItem.getShippingMarks(),
                    utilizedTransportEquipmentTOS
                        .get(0)
                        .getCargoItems()
                        .get(0)
                        .getCargoLineItems()
                        .get(0)
                        .getShippingMarks());
              })
          .verifyComplete();
    }
  }
}