package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Booking;
import org.dcsa.core.events.model.enums.DocumentStatus;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface BookingRepository
    extends ReactiveSortingRepository<Booking, UUID>, BookingCustomRepository {

  @Query(
      "SELECT DISTINCT b.carrier_booking_reference FROM booking b "
          + "JOIN shipment s ON b.carrier_booking_reference = s.carrier_booking_reference "
          + "JOIN cargo_item ci ON s.id = ci.shipment_id "
          + "JOIN shipping_instruction si ON ci.shipping_instruction_id = si.id "
          + "JOIN transport_document td ON si.id = td.shipping_instruction_id "
          + "WHERE td.transport_document_reference = :transportDocumentRef")
  Flux<String> findCarrierBookingRefsByTransportDocumentRef(String transportDocumentRef);

  @Query(
      "SELECT DISTINCT b.carrier_booking_reference FROM booking b "
          + "JOIN shipment s ON b.carrier_booking_reference = s.carrier_booking_reference "
          + "JOIN cargo_item ci ON s.id = ci.shipment_id "
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

  @Modifying
  @Query("UPDATE booking SET vessel_id = :vesselId where id = :id")
  Mono<Boolean> setVesselIDFor(UUID vesselId, UUID id);

  @Modifying
  @Query(
      "UPDATE booking SET invoice_payable_at = :invoicePayableAt where carrier_booking_request_reference = :carrierBookingRequestReference")
  Mono<Boolean> setInvoicePayableAtFor(
      String invoicePayableAt, String carrierBookingRequestReference);

  @Modifying
  @Query(
      "UPDATE booking SET place_of_issue = :placeOfIssue where carrier_booking_request_reference = :carrierBookingRequestReference")
  Mono<Boolean> setPlaceOfIssueIDFor(String placeOfIssue, String carrierBookingRequestReference);

  @Modifying
  @Query(
      "UPDATE booking SET document_status = :documentStatus where carrier_booking_request_reference = :carrierBookingRequestReference")
  Mono<Boolean> updateDocumentStatusForCarrierBookingRequestReference(
      DocumentStatus documentStatus, String carrierBookingRequestReference);
}
