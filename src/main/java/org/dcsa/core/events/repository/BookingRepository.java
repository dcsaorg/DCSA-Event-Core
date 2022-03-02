package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Booking;
import org.dcsa.core.events.model.enums.ShipmentEventTypeCode;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public interface BookingRepository
    extends ReactiveSortingRepository<Booking, UUID>, BookingCustomRepository {

  @Query(
      "SELECT DISTINCT b.carrier_booking_reference FROM booking b "
          + "JOIN shipment s ON b.carrier_booking_reference = s.carrier_booking_reference "
          + "JOIN shipment_equipment se ON s.id = se.shipment_id "
          + "JOIN cargo_item ci ON se.id = ci.shipment_equipment_id "
          + "JOIN shipping_instruction si ON ci.shipping_instruction_id = si.id "
          + "JOIN transport_document td ON si.id = td.shipping_instruction_id "
          + "WHERE td.transport_document_reference = :transportDocumentRef")
  Flux<String> findCarrierBookingRefsByTransportDocumentRef(String transportDocumentRef);

  @Query(
      "SELECT DISTINCT b.carrier_booking_reference FROM booking b "
          + "JOIN shipment s ON b.carrier_booking_reference = s.carrier_booking_reference "
          + "JOIN shipment_equipment se ON s.id = se.shipment_id "
          + "JOIN cargo_item ci ON se.id = ci.shipment_equipment_id "
          + "WHERE ci.shipping_instruction_id = :shippingInstructionID")
  Flux<String> findCarrierBookingRefsByShippingInstructionID(String shippingInstructionID);

  @Query(
      "SELECT DISTINCT b.carrier_booking_reference FROM booking b "
          + "JOIN shipment s ON b.carrier_booking_reference = s.carrier_booking_reference "
          + "JOIN shipment_transport st ON s.id = st.shipment_id "
          + "JOIN transport t ON st.transport_id = t.id "
          + "WHERE t.load_transport_call_id = :transportCallID")
  Flux<String> findCarrierBookingRefsByTransportCallID(String transportCallID);

  Mono<Booking> findByCarrierBookingRequestReference(String carrierBookingRequestReference);

  Flux<Booking> findAllOrderByBookingRequestDateTime(Example example, Pageable pageable);

  @Modifying
  @Query("UPDATE booking SET vessel_id = :vesselId where id = :id")
  Mono<Boolean> setVesselIDFor(UUID vesselId, UUID id);

  @Modifying
  @Query("UPDATE booking SET invoice_payable_at = :invoicePayableAt where id = :id")
  Mono<Boolean> setInvoicePayableAtFor(String invoicePayableAt, UUID id);

  @Modifying
  @Query("UPDATE booking SET place_of_issue = :placeOfIssue where id = :id")
  Mono<Boolean> setPlaceOfIssueIDFor(String placeOfIssue, UUID id);

  @Modifying
  @Query(
      "UPDATE booking SET document_status = :documentStatus, updated_date_time = :updatedDateTime where carrier_booking_request_reference = :carrierBookingRequestReference")
  Mono<Boolean> updateDocumentStatusAndUpdatedDateTimeForCarrierBookingRequestReference(
      ShipmentEventTypeCode documentStatus,
      String carrierBookingRequestReference,
      OffsetDateTime updatedDateTime);

    @Query(
      "SELECT DISTINCT b.* FROM cargo_item ci "
          + "JOIN shipment_equipment se ON se.id = ci.shipment_equipment_id "
          + "JOIN shipment s ON s.id = se.shipment_id "
          + "JOIN booking b ON b.id = s.booking_id "
          + "WHERE s.carrier_booking_reference = :carrierBookingReference")
  Flux<Booking> findAllByCarrierBookingReference(String carrierBookingReference);

  @Query(
      "SELECT DISTINCT b.* FROM shipping_instruction si "
          + "JOIN cargo_item ci ON ci.shipping_instruction_id = si.id "
          + "JOIN shipment_equipment se ON se.id = ci.shipment_equipment_id "
          + "JOIN shipment s ON s.id = se.shipment_id "
          + "JOIN booking b ON b.id = s.booking_id "
          + "WHERE si.id = :shippingInstructionID")
  Flux<Booking> findAllByShippingInstructionID(String shippingInstructionID);

}
