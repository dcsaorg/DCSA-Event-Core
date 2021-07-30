package org.dcsa.core.events.util;

import org.apache.commons.lang3.StringUtils;
import org.dcsa.core.events.exception.BadRequestException;
import org.dcsa.core.events.model.enums.*;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

/**
 * Common events request validation class
 * Will be used to create specific implementations
 */
public abstract class EventsRequestValidation extends RequestValidationUtils
    implements RequestValidator {

  protected CompletableFuture<Void> specificValidations = CompletableFuture.runAsync(() -> {});

  @Override
  public void validate(Map<String, String> queryParams) {
    try {
      addCommonEventValidations(queryParams).get();
    } catch (InterruptedException | ExecutionException e) {
      throw new BadRequestException();
    }
  }

  private CompletableFuture<Void> addCommonEventValidations(Map<String, String> queryParams) {

    CompletableFuture<Void> commonValidations =
        CompletableFuture.runAsync(
                () -> validateTransportEventTypeCode(queryParams.get("transportEventTypeCode")))
            .thenRunAsync(() -> validateTransportCallID(queryParams.get("transportCallID")))
            .thenRunAsync(() -> validateVesselIMONumber(queryParams.get("vesselIMONumber")))
            .thenRunAsync(() -> validateCarrierVoyageNumber(queryParams.get("carrierVoyageNumber")))
            .thenRunAsync(() -> validateCarrierServiceCode(queryParams.get("carrierServiceCode")))
            .thenRunAsync(() -> validateLimit(queryParams.get("limit")));

    return CompletableFuture.allOf(specificValidations, commonValidations);
  }

  protected void validateTNTEventType(String eventType) {
    if (org.springframework.util.StringUtils.hasLength(eventType)) {

      Stream<String> eventsTypeStream =
          eventType.contains(",") ? Arrays.stream(eventType.split(",")) : Stream.of(eventType);

      eventsTypeStream.forEach(
          e ->
              checkIfValidEnumAndPartOf(
                  e,
                  EventType.class,
                  Stream.of(EventType.SHIPMENT, EventType.TRANSPORT, EventType.EQUIPMENT)));
    }
  }

  protected void validateShipmentEventTypeCode(String shipmentEventTypeCode) {
    if (org.springframework.util.StringUtils.hasLength(shipmentEventTypeCode)) {
      Stream<String> shipmentEventTypeStream =
          shipmentEventTypeCode.contains(",")
              ? Arrays.stream(shipmentEventTypeCode.split(","))
              : Stream.of(shipmentEventTypeCode);

      shipmentEventTypeStream.forEach(e -> checkIfValidEnum(e, ShipmentEventTypeCode.class));
    }
  }

  protected void validateCarrierBookingReference(String carrierBookingReference) {
    if (org.springframework.util.StringUtils.hasLength(carrierBookingReference)) {
      validMaxLength(35, StringUtils.length(carrierBookingReference));
    }
  }

  protected void validateTransportDocumentReference(String transportDocumentReference) {
    if (org.springframework.util.StringUtils.hasLength(transportDocumentReference)) {
      validMaxLength(20, StringUtils.length(transportDocumentReference));
    }
  }

  protected void validateTransportDocumentTypeCode(String transportDocumentTypeCode) {
    if (org.springframework.util.StringUtils.hasLength(transportDocumentTypeCode)) {
      Stream<String> transportDocumentTypeCodeStream =
          transportDocumentTypeCode.contains(",")
              ? Arrays.stream(transportDocumentTypeCode.split(","))
              : Stream.of(transportDocumentTypeCode);

      transportDocumentTypeCodeStream.forEach(
          e -> checkIfValidEnum(e, TransportDocumentTypeCode.class));
    }
  }

  protected void validateTransportEventTypeCode(String transportEventTypeCode) {
    if (org.springframework.util.StringUtils.hasLength(transportEventTypeCode)) {
      Stream<String> transportEventTypeCodeStream =
          transportEventTypeCode.contains(",")
              ? Arrays.stream(transportEventTypeCode.split(","))
              : Stream.of(transportEventTypeCode);

      transportEventTypeCodeStream.forEach(e -> checkIfValidEnum(e, TransportEventTypeCode.class));
    }
  }

  protected void validateTransportCallID(String transportCallID) {
    if (org.springframework.util.StringUtils.hasLength(transportCallID)) {
      validMaxLength(100, StringUtils.length(transportCallID));
    }
  }

  protected void validateVesselIMONumber(String vesselIMONumber) {
    if (org.springframework.util.StringUtils.hasLength(vesselIMONumber)) {
      validMaxLength(7, StringUtils.length(vesselIMONumber));
    }
  }

  protected void validateCarrierVoyageNumber(String carrierVoyageNumber) {
    if (org.springframework.util.StringUtils.hasLength(carrierVoyageNumber)) {
      validMaxLength(50, StringUtils.length(carrierVoyageNumber));
    }
  }

  protected void validateCarrierServiceCode(String carrierServiceCode) {
    if (org.springframework.util.StringUtils.hasLength(carrierServiceCode)) {
      validMaxLength(5, StringUtils.length(carrierServiceCode));
    }
  }

  protected void validateEquipmentEventTypeCode(String equipmentEventTypeCode) {
    if (org.springframework.util.StringUtils.hasLength(equipmentEventTypeCode)) {
      Stream<String> equipmentEventTypeCodeStream =
          equipmentEventTypeCode.contains(",")
              ? Arrays.stream(equipmentEventTypeCode.split(","))
              : Stream.of(equipmentEventTypeCode);

      equipmentEventTypeCodeStream.forEach(e -> checkIfValidEnum(e, EquipmentEventTypeCode.class));
    }
  }

  protected void validateEquipmentReference(String equipmentReference) {
    if (org.springframework.util.StringUtils.hasLength(equipmentReference)) {
      validMaxLength(15, StringUtils.length(equipmentReference));
    }
  }

  protected void validateLimit(String limit) {
    if (org.springframework.util.StringUtils.hasLength(limit) && Integer.parseInt(limit) < 1) {
      throw new BadRequestException();
    }
  }
}
