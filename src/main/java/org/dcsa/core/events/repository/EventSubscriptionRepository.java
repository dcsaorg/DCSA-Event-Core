package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.*;
import org.dcsa.core.events.model.enums.*;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface EventSubscriptionRepository extends ExtendedRepository<EventSubscription, UUID> {

  @Modifying
  @Query(
      "INSERT INTO event_subscription_event_types (subscription_id, event_type)"
          + " VALUES (:subscriptionID, :eventType)")
  Mono<Void> insertEventTypeForSubscription(UUID subscriptionID, EventType eventType);

  @Modifying
  @Query(
      "INSERT INTO event_subscription_transport_document_type (subscription_id, transport_document_type_code)"
          + " VALUES (:subscriptionID, :transportDocumentTypeCode)")
  Mono<Void> insertTransportDocumentEventTypeForSubscription(
      UUID subscriptionID, TransportDocumentTypeCode transportDocumentTypeCode);

  @Modifying
  @Query(
      "INSERT INTO event_subscription_shipment_event_type (subscription_id, shipment_event_type_code)"
          + " VALUES (:subscriptionID, :shipmentEventTypeCode)")
  Mono<Void> insertShipmentEventTypeForSubscription(
      UUID subscriptionID, ShipmentEventTypeCode shipmentEventTypeCode);

  @Modifying
  @Query(
      "INSERT INTO event_subscription_transport_event_type (subscription_id, transport_event_type_code)"
          + " VALUES (:subscriptionID, :transportEventTypeCode)")
  Mono<Void> insertTransportEventTypeForSubscription(
      UUID subscriptionID, TransportEventTypeCode transportEventTypeCode);

  @Modifying
  @Query(
      "INSERT INTO event_subscription_equipment_event_type (subscription_id, equipment_event_type_code)"
          + " VALUES (:subscriptionID, :equipmentEventTypeCode)")
  Mono<Void> insertEquipmentEventTypeForSubscription(
      UUID subscriptionID, EquipmentEventTypeCode equipmentEventTypeCode);

  @Modifying
  @Query(
          "INSERT INTO event_subscription_operations_event_type (subscription_id, operations_event_type_code)"
                  + " VALUES (:subscriptionID, :operationsEventTypeCode)")
  Mono<Void> insertOperationsEventTypeForSubscription(
          UUID subscriptionID, OperationsEventTypeCode operationsEventTypeCode);

  @Query(
      "SELECT event_type FROM event_subscription_event_types"
          + " WHERE subscription_id = :subscriptionID")
  Flux<String> findEventTypesForSubscription(UUID subscriptionID);

  @Query(
      "SELECT transport_document_type_code FROM event_subscription_transport_document_type"
          + " WHERE subscription_id = :subscriptionID")
  Flux<String> findTransportDocumentEventTypesForSubscription(UUID subscriptionID);

  @Query(
      "SELECT transport_event_type_code FROM event_subscription_transport_event_type"
          + " WHERE subscription_id = :subscriptionID")
  Flux<String> findTransportEventTypesForSubscriptionID(UUID subscriptionID);

  @Query(
      "SELECT shipment_event_type_code FROM event_subscription_shipment_event_type"
          + " WHERE subscription_id = :subscriptionID")
  Flux<String> findShipmentEventTypesForSubscriptionID(UUID subscriptionID);

  @Query(
      "SELECT equipment_event_type_code FROM event_subscription_equipment_event_type"
          + " WHERE subscription_id = :subscriptionID")
  Flux<String> findEquipmentEventTypesForSubscriptionID(UUID subscriptionID);

  @Query(
          "SELECT operations_event_type_code FROM event_subscription_operations_event_type"
                  + " WHERE subscription_id = :subscriptionID")
  Flux<String> findOperationsEventTypesForSubscriptionID(UUID subscriptionID);

  @Modifying
  @Query("DELETE FROM event_subscription_event_types WHERE subscription_id = :subscriptionID")
  Mono<Void> deleteEventTypesForSubscription(UUID subscriptionID);

  @Modifying
  @Query(
      "DELETE FROM event_subscription_transport_document_type WHERE subscription_id = :subscriptionID")
  Mono<Void> deleteTransportDocumentEventTypesForSubscriptionID(UUID subscriptionID);

  @Modifying
  @Query(
      "DELETE FROM event_subscription_transport_event_type WHERE subscription_id = :subscriptionID")
  Mono<Void> deleteTransportEventTypesForSubscriptionID(UUID subscriptionID);

  @Modifying
  @Query(
      "DELETE FROM event_subscription_shipment_event_type WHERE subscription_id = :subscriptionID")
  Mono<Void> deleteShipmentEventTypesForSubscriptionID(UUID subscriptionID);

  @Modifying
  @Query(
      "DELETE FROM event_subscription_equipment_event_type WHERE subscription_id = :subscriptionID")
  Mono<Void> deleteEquipmentEventTypesForSubscriptionID(UUID subscriptionID);

  @Modifying
  @Query(
          "DELETE FROM event_subscription_operations_event_type WHERE subscription_id = :subscriptionID")
  Mono<Void> deleteOperationsEventTypesForSubscriptionID(UUID subscriptionID);

  @Query(
      "SELECT event_subscription_event_types.* FROM event_subscription_event_types"
          + " WHERE subscription_id IN (:subscriptionIDs)"
          + " ORDER BY subscription_id, event_type")
  Flux<EventSubscriptionEventType> findEventTypesForSubscriptionIDIn(List<UUID> subscriptionIDs);

  @Query(
      "SELECT event_subscription_transport_document_type.* FROM event_subscription_transport_document_type"
          + " WHERE subscription_id IN (:subscriptionIDs)"
          + " ORDER BY subscription_id, transport_document_type_code")
  Flux<EventSubscriptionTransportDocumentEventType>
      findTransportDocumentEventTypesForSubscriptionIDIn(List<UUID> subscriptionIDs);

  @Query(
      "SELECT event_subscription_transport_event_type.* FROM event_subscription_transport_event_type"
          + " WHERE subscription_id IN (:subscriptionIDs)"
          + " ORDER BY subscription_id, transport_event_type_code")
  Flux<EventSubscriptionTransportEventType> findTransportEventTypesForSubscriptionIDIn(
      List<UUID> subscriptionIDs);

  @Query(
      "SELECT event_subscription_shipment_event_type.* FROM event_subscription_shipment_event_type"
          + " WHERE subscription_id IN (:subscriptionIDs)"
          + " ORDER BY subscription_id, shipment_event_type_code")
  Flux<EventSubscriptionShipmentEventType> findShipmentEventTypesForSubscriptionIDIn(
      List<UUID> subscriptionIDs);

  @Query(
      "SELECT event_subscription_equipment_event_type.* FROM event_subscription_equipment_event_type"
          + " WHERE subscription_id IN (:subscriptionIDs)"
          + " ORDER BY subscription_id, equipment_event_type_code")
  Flux<EventSubscriptionEquipmentEventType> findEquipmentEventTypesForSubscriptionIDIn(
      List<UUID> subscriptionIDs);

  @Query(
          "SELECT event_subscription_operations_event_type.* FROM event_subscription_operations_event_type"
                  + " WHERE subscription_id IN (:subscriptionIDs)"
                  + " ORDER BY subscription_id, operations_event_type_code")
  Flux<EventSubscriptionOperationsEventType> findOperationsEventTypesForSubscriptionIDIn(
          List<UUID> subscriptionIDs);

  @Query(
      "SELECT es.* FROM event_subscription es"
          + " JOIN event_subscription_event_types eset"
          + "   ON eset.subscription_id = es.subscription_id"
          + " LEFT JOIN event_subscription_transport_document_type estdt"
          + "   ON estdt.subscription_id = es.subscription_id"
          + " LEFT JOIN event_subscription_equipment_event_type eseet"
          + "   ON eseet.subscription_id = es.subscription_id"
          + " WHERE eset.event_type = 'EQUIPMENT'"
          + "   AND (eseet.equipment_event_type_code IS NULL OR eseet.equipment_event_type_code = :equipmentEventTypeCode)"
          + "   AND (es.equipment_reference IS NULL OR es.equipment_reference = (:equipmentReference))"
          + "   AND (es.carrier_booking_reference IS NULL OR es.carrier_booking_reference IN (:carrierBookingReferences))"
          + "   AND (es.transport_document_reference IS NULL OR es.transport_document_reference IN (:transportDocumentReferences))"
          + "   AND (estdt.transport_document_type_code IS NULL OR estdt.transport_document_type_code = (:transportDocumentTypeCodes))"
          + "   AND (es.transport_call_reference IS NULL OR es.transport_call_reference = :transportCallReference)"
          + "   AND (es.vessel_imo_number IS NULL OR es.vessel_imo_number = :vesselIMONumber)"
          + "   AND (es.carrier_voyage_number IS NULL OR es.carrier_voyage_number IN (:carrierVoyageNumbers))"
          + "   AND (es.carrier_service_code IS NULL OR es.carrier_service_code IN (:carrierServiceCodes))")
  Flux<EventSubscription> findByEquipmentEventFields(
      EquipmentEventTypeCode equipmentEventTypeCode,
      String equipmentReference,
      List<String> carrierBookingReferences,
      List<String> transportDocumentReferences,
      List<String> transportDocumentTypeCodes,
      String transportCallReference,
      String vesselIMONumber,
      List<String> carrierVoyageNumbers,
      List<String> carrierServiceCodes);

  @Query(
      "SELECT es.* FROM event_subscription es"
          + " JOIN event_subscription_event_types eset"
          + "   ON eset.subscription_id = es.subscription_id"
          + " LEFT JOIN event_subscription_transport_document_type estdt"
          + "   ON estdt.subscription_id = es.subscription_id"
          + " LEFT JOIN event_subscription_shipment_event_type esset"
          + "   ON esset.subscription_id = es.subscription_id"
          + " WHERE eset.event_type = 'SHIPMENT'"
          + "   AND (esset.shipment_event_type_code IS NULL OR esset.shipment_event_type_code = :shipmentEventTypeCode)"
          + "   AND (es.carrier_booking_reference IS NULL OR es.carrier_booking_reference IN (:carrierBookingReferences))"
          + "   AND (es.transport_document_reference IS NULL OR es.transport_document_reference IN (:transportDocumentReferences))"
          + "   AND (estdt.transport_document_type_code IS NULL OR estdt.transport_document_type_code = (:transportDocumentTypeCodes))"
          + "   AND (es.transport_call_id IS NULL OR es.transport_call_id = :transportCallIDs)"
          + "   AND (es.vessel_imo_number IS NULL OR es.vessel_imo_number = :vesselIMONumbers)"
          + "   AND (es.carrier_voyage_number IS NULL OR es.carrier_voyage_number IN (:carrierVoyageNumbers))"
          + "   AND (es.carrier_service_code IS NULL OR es.carrier_service_code IN (:carrierServiceCodes))"
          + "   AND es.equipment_reference IS NULL OR es.equipment_reference IN (:equipmentReference)")
  Flux<EventSubscription> findByShipmentEventFields(
      ShipmentEventTypeCode shipmentEventTypeCode,
      List<String> carrierBookingReferences,
      List<String> transportDocumentReferences,
      List<String> transportDocumentTypeCodes,
      List<String> transportCallReferences,
      List<String> equipmentReferences,
      List<String> carrierServiceCodes,
      List<String> carrierVoyageNumbers,
      List<String> vesselIMONumbers);

  @Query(
      "SELECT es.* FROM event_subscription es"
          + " JOIN event_subscription_event_types eset"
          + "   ON eset.subscription_id = es.subscription_id"
          + " LEFT JOIN event_subscription_transport_document_type estdt"
          + "   ON estdt.subscription_id = es.subscription_id"
          + " LEFT JOIN event_subscription_transport_event_type estet"
          + "   ON estet.subscription_id = es.subscription_id"
          + " WHERE eset.event_type = 'TRANSPORT'"
          + "   AND (estet.transport_event_type_code IS NULL OR estet.transport_event_type_code = :transportEventTypeCode)"
          + "   AND (es.carrier_booking_reference IS NULL OR es.carrier_booking_reference IN (:carrierBookingReferences))"
          + "   AND (es.transport_document_reference IS NULL OR es.transport_document_reference IN (:transportDocumentReferences))"
          + "   AND (estdt.transport_document_type_code IS NULL OR estdt.transport_document_type_code IN (:transportDocumentTypeCodes))"
          + "   AND (es.transport_call_reference IS NULL OR es.transport_call_reference = :transportCallReference)"
          + "   AND (es.vessel_imo_number IS NULL OR es.vessel_imo_number = :vesselIMONumber)"
          + "   AND (es.carrier_voyage_number IS NULL OR es.carrier_voyage_number IN (:carrierVoyageNumbers))"
          + "   AND (es.carrier_service_code IS NULL OR es.carrier_service_code IN (:carrierServiceCodes))"
          + "   AND es.equipment_reference IS NULL")
  Flux<EventSubscription> findByTransportEventFields(
      List<String> carrierVoyageNumbers,
      List<String> carrierServiceCodes,
      TransportEventTypeCode transportEventTypeCode,
      String vesselIMONumber,
      String transportCallReference,
      List<String> carrierBookingReferences,
      List<String> transportDocumentReferences,
      List<String> transportDocumentTypeCodes);

  @Query(
      "SELECT es.* FROM event_subscription es"
          + " JOIN event_subscription_event_types eset"
          + "   ON eset.subscription_id = es.subscription_id"
          + " LEFT JOIN event_subscription_transport_document_type estdt"
          + "   ON estdt.subscription_id = es.subscription_id"
          + " LEFT JOIN event_subscription_operations_event_type esoet"
          + "   ON esoet.subscription_id = es.subscription_id"
          + " WHERE eset.event_type = 'OPERATIONS'"
          + "   AND (esoet.operations_event_type_code IS NULL OR esoet.operations_event_type_code = :operationsEventTypeCode)"
          + "   AND (es.carrier_booking_reference IS NULL OR es.carrier_booking_reference IN (:carrierBookingReferences))"
          + "   AND (es.transport_document_reference IS NULL OR es.transport_document_reference IN (:transportDocumentReferences))"
          + "   AND (estdt.transport_document_type_code IS NULL OR estdt.transport_document_type_code IN (:transportDocumentTypeCodes))"
          + "   AND (es.transport_call_reference IS NULL OR es.transport_call_reference = :transportCallReference)"
          + "   AND (es.vessel_imo_number IS NULL OR es.vessel_imo_number = :vesselIMONumber)"
          + "   AND (es.carrier_voyage_number IS NULL OR es.carrier_voyage_number IN (:carrierVoyageNumbers))"
          + "   AND (es.carrier_service_code IS NULL OR es.carrier_service_code IN (:carrierServiceCodes))"
          + "   AND es.equipment_reference IS NULL")
  Flux<EventSubscription> findByOperationEventFields(
      OperationsEventTypeCode operationsEventTypeCode,
      List<String> carrierBookingReferences,
      List<String> transportDocumentReferences,
      List<String> transportDocumentTypeCodes,
      String transportCallReference,
      String vesselIMONumber,
      List<String> carrierVoyageNumbers,
      List<String> carrierServiceCodes);
}
