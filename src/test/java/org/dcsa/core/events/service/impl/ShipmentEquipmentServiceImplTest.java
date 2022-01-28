package org.dcsa.core.events.service.impl;

import org.dcsa.core.events.model.*;
import org.dcsa.core.events.model.enums.*;
import org.dcsa.core.events.model.transferobjects.*;
import org.dcsa.core.events.repository.*;
import org.dcsa.core.events.service.ReferenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test for ShipmentEquipmentService implementation")
public class ShipmentEquipmentServiceImplTest {

  @Mock ShipmentEquipmentRepository shipmentEquipmentRepository;
  @Mock EquipmentRepository equipmentRepository;
  @Mock SealRepository sealRepository;
  @Mock ActiveReeferSettingsRepository activeReeferSettingsRepository;
  @Mock CargoItemRepository cargoItemRepository;
  @Mock CargoLineItemRepository cargoLineItemRepository;
  @Mock ReferenceService referenceService;

  @InjectMocks ShipmentEquipmentServiceImpl shipmentEquipmentService;

  CargoLineItem cargoLineItem;
  CargoItem cargoItem;
  Seal seal;
  ActiveReeferSettings activeReeferSettings;
  Equipment equipment;
  ShipmentEquipment shipmentEquipment;
  ShipmentEquipmentTO shipmentEquipmentTO;
  ReferenceTO referenceTO;

  @BeforeEach
  void init() {
    initEntities(
        UUID.randomUUID(), UUID.randomUUID(), "equipmentReference1", "shippingInstructionID1");
    initTOs();
  }

  private void initEntities(
      UUID shipmentID,
      UUID shipmentEquipmentId,
      String equipmentReference,
      String shippingInstructionId) {
    shipmentEquipment = new ShipmentEquipment();
    shipmentEquipment.setId(UUID.randomUUID());
    shipmentEquipment.setCargoGrossWeight(120.0F);
    shipmentEquipment.setCargoGrossWeightUnit(WeightUnit.KGM);
    shipmentEquipment.setShipmentID(shipmentID);
    shipmentEquipment.setEquipmentReference(equipmentReference);

    equipment = new Equipment();
    equipment.setEquipmentReference(equipmentReference);
    equipment.setIsoEquipmentCode("ISO1");
    equipment.setWeightUnit("KGM");
    equipment.setTareWeight(120F);

    activeReeferSettings = new ActiveReeferSettings();
    activeReeferSettings.setShipmentEquipmentID(shipmentEquipmentId);
    activeReeferSettings.setVentilationMax(10F);
    activeReeferSettings.setVentilationMin(5F);
    activeReeferSettings.setTemperatureUnit(TemperatureUnit.CEL);
    activeReeferSettings.setTemperatureMax(8F);
    activeReeferSettings.setTemperatureMin(5F);
    activeReeferSettings.setHumidityMin(30F);
    activeReeferSettings.setHumidityMax(50F);

    seal = new Seal();
    seal.setId(UUID.randomUUID());
    seal.setShipmentEquipmentID(shipmentEquipmentId);
    seal.setSealType("BLT");
    seal.setSealSource("CAR");
    seal.setSealNumber("1");

    cargoItem = new CargoItem();
    cargoItem.setId(UUID.randomUUID());
    cargoItem.setShipmentEquipmentID(shipmentEquipmentId);
    cargoItem.setShippingInstructionID(shippingInstructionId);
    cargoItem.setShipmentID(shipmentID);
    cargoItem.setPackageCode("ABC");
    cargoItem.setHsCode("testHSCode");
    cargoItem.setNumberOfPackages(1);
    cargoItem.setWeight(100F);
    cargoItem.setWeightUnit(WeightUnit.KGM);
    cargoItem.setVolume(400F);
    cargoItem.setVolumeUnit(VolumeUnit.CBM);
    cargoItem.setDescriptionOfGoods("Goods description");

    cargoLineItem = new CargoLineItem();
    cargoLineItem.setCargoItemID(cargoItem.getId());
    cargoLineItem.setCargoLineItemID("CargoLineItem");
    cargoLineItem.setShippingMarks("shippingMarks");
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
    cargoItemTO.setCarrierBookingReference("carrierBookingReference1");
    cargoItemTO.setWeight(100F);
    cargoItemTO.setWeightUnit(WeightUnit.KGM);
    cargoItemTO.setVolume(400F);
    cargoItemTO.setVolumeUnit(VolumeUnit.CBM);
    cargoItemTO.setPackageCode("ABC");
    cargoItemTO.setHsCode("testHSCode");
    cargoItemTO.setNumberOfPackages(1);
    cargoItemTO.setDescriptionOfGoods("Goods description");
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

    shipmentEquipmentTO = new ShipmentEquipmentTO();
    shipmentEquipmentTO.setCargoGrossWeight(120.0F);
    shipmentEquipmentTO.setCargoGrossWeightUnit(WeightUnit.KGM);
    shipmentEquipmentTO.setEquipment(equipmentTO);
    shipmentEquipmentTO.setSeals(Collections.singletonList(sealTO));
    shipmentEquipmentTO.setCargoItems(Collections.singletonList(cargoItemTO));
    shipmentEquipmentTO.setActiveReeferSettings(activeReeferSettingsTO);
  }

  @Test
  @DisplayName("Test create ShipmentEquipment")
  void testCreateShipmentEquipmentService() {
    UUID shipmentID = UUID.randomUUID();
    String shippingInstructionID = "shippingInstructionID1";
    UUID shipmentEquipmentID = UUID.randomUUID();
    shipmentEquipment.setShipmentID(shipmentID);
    shipmentEquipment.setId(shipmentEquipmentID);
    cargoItem.setShipmentID(shipmentID);
    cargoItem.setShippingInstructionID(shippingInstructionID);

    when(shipmentEquipmentRepository.save(any())).thenReturn(Mono.just(shipmentEquipment));
    when(equipmentRepository.save(eq(equipment))).thenReturn(Mono.just(equipment));
    when(sealRepository.save(any())).thenReturn(Mono.just(seal));
    when(activeReeferSettingsRepository.save(any())).thenReturn(Mono.just(activeReeferSettings));
    when(cargoItemRepository.save(any())).thenReturn(Mono.just(cargoItem));
    when(cargoLineItemRepository.save(any())).thenReturn(Mono.just(cargoLineItem));
    when(referenceService.createReferencesByShippingInstructionIDAndTOs(
            eq(shippingInstructionID), any()))
        .thenReturn(Mono.just(List.of(referenceTO)));

    ArgumentCaptor<ShipmentEquipment> argumentCaptorShipmentEquipment =
        ArgumentCaptor.forClass(ShipmentEquipment.class);
    ArgumentCaptor<CargoItem> argumentCaptorCargoItem = ArgumentCaptor.forClass(CargoItem.class);
    ArgumentCaptor<ActiveReeferSettings> argumentCaptorActiveReeferSettings =
        ArgumentCaptor.forClass(ActiveReeferSettings.class);
    ArgumentCaptor<Seal> argumentCaptorSeal = ArgumentCaptor.forClass(Seal.class);
    StepVerifier.create(
            shipmentEquipmentService.createShipmentEquipment(
                shipmentID, shippingInstructionID, Collections.singletonList(shipmentEquipmentTO)))
        .assertNext(
            shipmentEquipmentTOS -> {
              verify(shipmentEquipmentRepository).save(argumentCaptorShipmentEquipment.capture());
              verify(equipmentRepository).save(any());
              verify(sealRepository).save(argumentCaptorSeal.capture());
              verify(activeReeferSettingsRepository)
                  .save(argumentCaptorActiveReeferSettings.capture());
              verify(cargoItemRepository).save(argumentCaptorCargoItem.capture());
              verify(cargoLineItemRepository).save(any());
              verify(referenceService).createReferencesByShippingInstructionIDAndTOs(any(), any());
              assertEquals(shipmentID, argumentCaptorShipmentEquipment.getValue().getShipmentID());
              assertEquals(
                  shippingInstructionID,
                  argumentCaptorCargoItem.getValue().getShippingInstructionID());
              assertEquals(
                  shipmentEquipmentID,
                  argumentCaptorActiveReeferSettings.getValue().getShipmentEquipmentID());
              assertEquals(
                  shipmentEquipmentID, argumentCaptorSeal.getValue().getShipmentEquipmentID());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test create ShipmentEquipment without ActiveReeferSettings")
  void testCreateShipmentEquipmentWithoutActiveReeferSettings() {
    UUID shipmentID = UUID.randomUUID();
    String shippingInstructionID = "shippingInstructionID1";
    UUID shipmentEquipmentID = UUID.randomUUID();
    shipmentEquipment.setShipmentID(shipmentID);
    shipmentEquipment.setId(shipmentEquipmentID);
    cargoItem.setShipmentID(shipmentID);
    cargoItem.setShippingInstructionID(shippingInstructionID);
    shipmentEquipmentTO.setActiveReeferSettings(null);

    when(shipmentEquipmentRepository.save(any())).thenReturn(Mono.just(shipmentEquipment));
    when(equipmentRepository.save(eq(equipment))).thenReturn(Mono.just(equipment));
    when(sealRepository.save(any())).thenReturn(Mono.just(seal));
    when(cargoItemRepository.save(any())).thenReturn(Mono.just(cargoItem));
    when(cargoLineItemRepository.save(any())).thenReturn(Mono.just(cargoLineItem));
    when(referenceService.createReferencesByShippingInstructionIDAndTOs(
            eq(shippingInstructionID), any()))
        .thenReturn(Mono.just(List.of(referenceTO)));

    ArgumentCaptor<ShipmentEquipment> argumentCaptorShipmentEquipment =
        ArgumentCaptor.forClass(ShipmentEquipment.class);
    ArgumentCaptor<CargoItem> argumentCaptorCargoItem = ArgumentCaptor.forClass(CargoItem.class);
    ArgumentCaptor<Seal> argumentCaptorSeal = ArgumentCaptor.forClass(Seal.class);
    StepVerifier.create(
            shipmentEquipmentService.createShipmentEquipment(
                shipmentID, shippingInstructionID, Collections.singletonList(shipmentEquipmentTO)))
        .assertNext(
            shipmentEquipmentTOS -> {
              verify(shipmentEquipmentRepository).save(argumentCaptorShipmentEquipment.capture());
              verify(equipmentRepository).save(any());
              verify(sealRepository).save(argumentCaptorSeal.capture());
              verify(activeReeferSettingsRepository, never()).save(any());
              verify(cargoItemRepository).save(argumentCaptorCargoItem.capture());
              verify(cargoLineItemRepository).save(any());
              verify(referenceService).createReferencesByShippingInstructionIDAndTOs(any(), any());
              assertEquals(shipmentID, argumentCaptorShipmentEquipment.getValue().getShipmentID());
              assertEquals(
                  shippingInstructionID,
                  argumentCaptorCargoItem.getValue().getShippingInstructionID());
              assertEquals(
                  shipmentEquipmentID, argumentCaptorSeal.getValue().getShipmentEquipmentID());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test create ShipmentEquipment without Seals")
  void testCreateShipmentEquipmentWithoutSeals() {
    UUID shipmentID = UUID.randomUUID();
    String shippingInstructionID = "shippingInstructionID1";
    UUID shipmentEquipmentID = UUID.randomUUID();
    shipmentEquipment.setShipmentID(shipmentID);
    shipmentEquipment.setId(shipmentEquipmentID);
    cargoItem.setShipmentID(shipmentID);
    cargoItem.setShippingInstructionID(shippingInstructionID);
    shipmentEquipmentTO.setSeals(null);

    when(shipmentEquipmentRepository.save(any())).thenReturn(Mono.just(shipmentEquipment));
    when(equipmentRepository.save(eq(equipment))).thenReturn(Mono.just(equipment));
    when(activeReeferSettingsRepository.save(any())).thenReturn(Mono.just(activeReeferSettings));
    when(cargoItemRepository.save(any())).thenReturn(Mono.just(cargoItem));
    when(cargoLineItemRepository.save(any())).thenReturn(Mono.just(cargoLineItem));
    when(referenceService.createReferencesByShippingInstructionIDAndTOs(
            eq(shippingInstructionID), any()))
        .thenReturn(Mono.just(List.of(referenceTO)));

    ArgumentCaptor<ShipmentEquipment> argumentCaptorShipmentEquipment =
        ArgumentCaptor.forClass(ShipmentEquipment.class);
    ArgumentCaptor<CargoItem> argumentCaptorCargoItem = ArgumentCaptor.forClass(CargoItem.class);
    ArgumentCaptor<ActiveReeferSettings> argumentCaptorActiveReeferSettings =
        ArgumentCaptor.forClass(ActiveReeferSettings.class);
    StepVerifier.create(
            shipmentEquipmentService.createShipmentEquipment(
                shipmentID, shippingInstructionID, Collections.singletonList(shipmentEquipmentTO)))
        .assertNext(
            shipmentEquipmentTOS -> {
              verify(shipmentEquipmentRepository).save(argumentCaptorShipmentEquipment.capture());
              verify(equipmentRepository).save(any());
              verify(sealRepository, never()).save(any());
              verify(activeReeferSettingsRepository)
                  .save(argumentCaptorActiveReeferSettings.capture());
              verify(cargoItemRepository).save(argumentCaptorCargoItem.capture());
              verify(cargoLineItemRepository).save(any());
              verify(referenceService).createReferencesByShippingInstructionIDAndTOs(any(), any());
              assertEquals(shipmentID, argumentCaptorShipmentEquipment.getValue().getShipmentID());
              assertEquals(
                  shippingInstructionID,
                  argumentCaptorCargoItem.getValue().getShippingInstructionID());
              assertEquals(
                  shipmentEquipmentID,
                  argumentCaptorActiveReeferSettings.getValue().getShipmentEquipmentID());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test create shipmentEquipment without CargoItems")
  void testCreateShipmentEquipmentWithoutCargoItems() {
    UUID shipmentID = UUID.randomUUID();
    String shippingInstructionID = "shippingInstructionID1";
    UUID shipmentEquipmentID = UUID.randomUUID();
    shipmentEquipment.setShipmentID(shipmentID);
    shipmentEquipment.setId(shipmentEquipmentID);
    shipmentEquipmentTO.setCargoItems(null);

    when(shipmentEquipmentRepository.save(any())).thenReturn(Mono.just(shipmentEquipment));
    when(equipmentRepository.save(eq(equipment))).thenReturn(Mono.just(equipment));
    when(sealRepository.save(any())).thenReturn(Mono.just(seal));
    when(activeReeferSettingsRepository.save(any())).thenReturn(Mono.just(activeReeferSettings));

    ArgumentCaptor<ShipmentEquipment> argumentCaptorShipmentEquipment =
        ArgumentCaptor.forClass(ShipmentEquipment.class);
    ArgumentCaptor<ActiveReeferSettings> argumentCaptorActiveReeferSettings =
        ArgumentCaptor.forClass(ActiveReeferSettings.class);
    ArgumentCaptor<Seal> argumentCaptorSeal = ArgumentCaptor.forClass(Seal.class);
    StepVerifier.create(
            shipmentEquipmentService.createShipmentEquipment(
                shipmentID, shippingInstructionID, Collections.singletonList(shipmentEquipmentTO)))
        .assertNext(
            shipmentEquipmentTOS -> {
              verify(shipmentEquipmentRepository).save(argumentCaptorShipmentEquipment.capture());
              verify(equipmentRepository).save(any());
              verify(sealRepository).save(argumentCaptorSeal.capture());
              verify(activeReeferSettingsRepository)
                  .save(argumentCaptorActiveReeferSettings.capture());
              verify(cargoItemRepository, never()).save(any());
              verify(cargoLineItemRepository, never()).save(any());
              assertEquals(shipmentID, argumentCaptorShipmentEquipment.getValue().getShipmentID());
              assertEquals(
                  shipmentEquipmentID,
                  argumentCaptorActiveReeferSettings.getValue().getShipmentEquipmentID());
              assertEquals(
                  shipmentEquipmentID, argumentCaptorSeal.getValue().getShipmentEquipmentID());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test create shipmentEquipment without CargoLineItems")
  void testCreateShipmentEquipmentWithoutCargoLineItems() {
    UUID shipmentID = UUID.randomUUID();
    String shippingInstructionID = "shippingInstructionID1";
    UUID shipmentEquipmentID = UUID.randomUUID();
    shipmentEquipment.setShipmentID(shipmentID);
    shipmentEquipment.setId(shipmentEquipmentID);
    cargoItem.setShipmentID(shipmentID);
    cargoItem.setShippingInstructionID(shippingInstructionID);
    shipmentEquipmentTO.getCargoItems().forEach(cargoItemTO -> cargoItemTO.setCargoLineItems(null));

    when(shipmentEquipmentRepository.save(any())).thenReturn(Mono.just(shipmentEquipment));
    when(equipmentRepository.save(eq(equipment))).thenReturn(Mono.just(equipment));
    when(sealRepository.save(any())).thenReturn(Mono.just(seal));
    when(activeReeferSettingsRepository.save(any())).thenReturn(Mono.just(activeReeferSettings));
    when(cargoItemRepository.save(any())).thenReturn(Mono.just(cargoItem));

    ArgumentCaptor<ShipmentEquipment> argumentCaptorShipmentEquipment =
        ArgumentCaptor.forClass(ShipmentEquipment.class);
    ArgumentCaptor<CargoItem> argumentCaptorCargoItem = ArgumentCaptor.forClass(CargoItem.class);
    ArgumentCaptor<ActiveReeferSettings> argumentCaptorActiveReeferSettings =
        ArgumentCaptor.forClass(ActiveReeferSettings.class);
    ArgumentCaptor<Seal> argumentCaptorSeal = ArgumentCaptor.forClass(Seal.class);
    StepVerifier.create(
            shipmentEquipmentService.createShipmentEquipment(
                shipmentID, shippingInstructionID, Collections.singletonList(shipmentEquipmentTO)))
        .assertNext(
            shipmentEquipmentTOS -> {
              verify(shipmentEquipmentRepository).save(argumentCaptorShipmentEquipment.capture());
              verify(equipmentRepository).save(any());
              verify(sealRepository).save(argumentCaptorSeal.capture());
              verify(activeReeferSettingsRepository)
                  .save(argumentCaptorActiveReeferSettings.capture());
              verify(cargoItemRepository).save(argumentCaptorCargoItem.capture());
              verify(cargoLineItemRepository, never()).save(any());
              assertEquals(shipmentID, argumentCaptorShipmentEquipment.getValue().getShipmentID());
              assertEquals(
                  shipmentEquipmentID,
                  argumentCaptorActiveReeferSettings.getValue().getShipmentEquipmentID());
              assertEquals(
                  shippingInstructionID,
                  argumentCaptorCargoItem.getValue().getShippingInstructionID());
              assertEquals(
                  shipmentEquipmentID, argumentCaptorSeal.getValue().getShipmentEquipmentID());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test create ShipmentEquipment with multiple seals")
  void testCreateShipmentEquipmentServiceWithMultipleSeals() {
    UUID shipmentID = UUID.randomUUID();
    String shippingInstructionID = "shippingInstructionID1";
    UUID shipmentEquipmentID = UUID.randomUUID();
    shipmentEquipment.setShipmentID(shipmentID);
    shipmentEquipment.setId(shipmentEquipmentID);
    cargoItem.setShipmentID(shipmentID);
    cargoItem.setShippingInstructionID(shippingInstructionID);

    SealTO sealTO = shipmentEquipmentTO.getSeals().get(0);
    SealTO additionalSealTO = shipmentEquipmentTO.getSeals().get(0);
    List<SealTO> sealTOList = List.of(sealTO, additionalSealTO);
    shipmentEquipmentTO.setSeals(sealTOList);

    when(shipmentEquipmentRepository.save(any())).thenReturn(Mono.just(shipmentEquipment));
    when(equipmentRepository.save(eq(equipment))).thenReturn(Mono.just(equipment));
    when(sealRepository.save(any())).thenReturn(Mono.just(seal));
    when(activeReeferSettingsRepository.save(any())).thenReturn(Mono.just(activeReeferSettings));
    when(cargoItemRepository.save(any())).thenReturn(Mono.just(cargoItem));
    when(cargoLineItemRepository.save(any())).thenReturn(Mono.just(cargoLineItem));
    when(referenceService.createReferencesByShippingInstructionIDAndTOs(
            eq(shippingInstructionID), any()))
        .thenReturn(Mono.just(List.of(referenceTO)));

    ArgumentCaptor<ShipmentEquipment> argumentCaptorShipmentEquipment =
        ArgumentCaptor.forClass(ShipmentEquipment.class);
    ArgumentCaptor<CargoItem> argumentCaptorCargoItem = ArgumentCaptor.forClass(CargoItem.class);
    ArgumentCaptor<ActiveReeferSettings> argumentCaptorActiveReeferSettings =
        ArgumentCaptor.forClass(ActiveReeferSettings.class);
    ArgumentCaptor<Seal> argumentCaptorSeal = ArgumentCaptor.forClass(Seal.class);
    StepVerifier.create(
            shipmentEquipmentService.createShipmentEquipment(
                shipmentID, shippingInstructionID, Collections.singletonList(shipmentEquipmentTO)))
        .assertNext(
            shipmentEquipmentTOS -> {
              verify(shipmentEquipmentRepository).save(argumentCaptorShipmentEquipment.capture());
              verify(equipmentRepository).save(any());
              verify(sealRepository, times(2)).save(argumentCaptorSeal.capture());
              verify(activeReeferSettingsRepository)
                  .save(argumentCaptorActiveReeferSettings.capture());
              verify(cargoItemRepository).save(argumentCaptorCargoItem.capture());
              verify(cargoLineItemRepository).save(any());
              verify(referenceService).createReferencesByShippingInstructionIDAndTOs(any(), any());
              assertEquals(shipmentID, argumentCaptorShipmentEquipment.getValue().getShipmentID());
              assertEquals(
                  shippingInstructionID,
                  argumentCaptorCargoItem.getValue().getShippingInstructionID());
              assertEquals(
                  shipmentEquipmentID,
                  argumentCaptorActiveReeferSettings.getValue().getShipmentEquipmentID());
              assertEquals(
                  shipmentEquipmentID, argumentCaptorSeal.getValue().getShipmentEquipmentID());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test create ShipmentEquipment with multiple cargoItems and multiple cargoLineItems")
  void testCreateShipmentEquipmentServiceWithCargoItems() {
    UUID shipmentID = UUID.randomUUID();
    String shippingInstructionID = "shippingInstructionID1";
    UUID shipmentEquipmentID = UUID.randomUUID();
    shipmentEquipment.setShipmentID(shipmentID);
    shipmentEquipment.setId(shipmentEquipmentID);
    cargoItem.setShipmentID(shipmentID);
    cargoItem.setShippingInstructionID(shippingInstructionID);

    CargoLineItemTO cargoLineItemTO1 = new CargoLineItemTO();
    cargoLineItemTO1.setCargoLineItemID("1");
    cargoLineItemTO1.setShippingMarks("mark1");

    CargoLineItemTO cargoLineItemTO2 = new CargoLineItemTO();
    cargoLineItemTO2.setCargoLineItemID("1");
    cargoLineItemTO2.setShippingMarks("mark1");

    CargoItemTO cargoItemTO1 = new CargoItemTO();
    cargoItemTO1.setHsCode("HSCode1");
    cargoItemTO1.setDescriptionOfGoods("cargoitem1");
    cargoItemTO1.setWeight(10F);
    cargoItemTO1.setWeightUnit(WeightUnit.KGM);
    cargoItemTO1.setNumberOfPackages(1);
    cargoItemTO1.setPackageCode("abc");
    cargoItemTO1.setCargoLineItems(List.of(cargoLineItemTO1, cargoLineItemTO2));

    CargoItemTO cargoItemTO2 = new CargoItemTO();
    cargoItemTO2.setHsCode("HSCode2");
    cargoItemTO2.setDescriptionOfGoods("cargoitem2");
    cargoItemTO2.setWeight(10F);
    cargoItemTO2.setWeightUnit(WeightUnit.KGM);
    cargoItemTO2.setNumberOfPackages(1);
    cargoItemTO2.setPackageCode("def");
    cargoItemTO2.setCargoLineItems(List.of(cargoLineItemTO1));

    shipmentEquipmentTO.setCargoItems(List.of(cargoItemTO1, cargoItemTO2));

    when(shipmentEquipmentRepository.save(any())).thenReturn(Mono.just(shipmentEquipment));
    when(equipmentRepository.save(eq(equipment))).thenReturn(Mono.just(equipment));
    when(sealRepository.save(any())).thenReturn(Mono.just(seal));
    when(activeReeferSettingsRepository.save(any())).thenReturn(Mono.just(activeReeferSettings));
    when(cargoItemRepository.save(any())).thenReturn(Mono.just(cargoItem));
    when(cargoLineItemRepository.save(any())).thenReturn(Mono.just(cargoLineItem));
    when(referenceService.createReferencesByShippingInstructionIDAndTOs(
            eq(shippingInstructionID), any()))
        .thenReturn(Mono.just(List.of(referenceTO)));

    ArgumentCaptor<ShipmentEquipment> argumentCaptorShipmentEquipment =
        ArgumentCaptor.forClass(ShipmentEquipment.class);
    ArgumentCaptor<CargoItem> argumentCaptorCargoItem = ArgumentCaptor.forClass(CargoItem.class);
    ArgumentCaptor<ActiveReeferSettings> argumentCaptorActiveReeferSettings =
        ArgumentCaptor.forClass(ActiveReeferSettings.class);
    ArgumentCaptor<Seal> argumentCaptorSeal = ArgumentCaptor.forClass(Seal.class);
    StepVerifier.create(
            shipmentEquipmentService.createShipmentEquipment(
                shipmentID, shippingInstructionID, Collections.singletonList(shipmentEquipmentTO)))
        .assertNext(
            shipmentEquipmentTOS -> {
              verify(shipmentEquipmentRepository).save(argumentCaptorShipmentEquipment.capture());
              verify(equipmentRepository).save(any());
              verify(sealRepository).save(argumentCaptorSeal.capture());
              verify(activeReeferSettingsRepository)
                  .save(argumentCaptorActiveReeferSettings.capture());
              verify(cargoItemRepository, times(2)).save(argumentCaptorCargoItem.capture());
              verify(cargoLineItemRepository, times(3)).save(any());
              verify(referenceService, times(2))
                  .createReferencesByShippingInstructionIDAndTOs(any(), any());
              assertEquals(shipmentID, argumentCaptorShipmentEquipment.getValue().getShipmentID());
              assertEquals(
                  shippingInstructionID,
                  argumentCaptorCargoItem.getValue().getShippingInstructionID());
              assertEquals(
                  shipmentEquipmentID,
                  argumentCaptorActiveReeferSettings.getValue().getShipmentEquipmentID());
              assertEquals(
                  shipmentEquipmentID, argumentCaptorSeal.getValue().getShipmentEquipmentID());
            })
        .verifyComplete();
  }
}
