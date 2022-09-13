package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Booking;
import org.dcsa.core.events.model.enums.ShipmentEventTypeCode;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public interface BookingRepository
    extends ExtendedRepository<Booking, UUID>, BookingCustomRepository {

  @Query(
      "SELECT * FROM booking b "
          + "WHERE b.carrier_booking_request_reference = :carrierBookingRequestReference "
          + "ORDER BY b.valid_until NULLS FIRST LIMIT 1")
  Mono<Booking> findByCarrierBookingRequestReferenceAndValidUntilIsNull(
      String carrierBookingRequestReference);

  @Deprecated
  Mono<Booking> findByCarrierBookingRequestReference(String carrierBookingRequestReference);

  Flux<Booking> findAllOrderByBookingRequestDateTime(Example example, Pageable pageable);

  @Modifying
  @Query("UPDATE booking SET vessel_id = :vesselId where id = :id")
  Mono<Boolean> setVesselIDFor(UUID vesselId, UUID id);

  @Modifying
  @Query("UPDATE booking SET invoice_payable_at_id = :invoicePayableAt where id = :id")
  Mono<Boolean> setInvoicePayableAtFor(UUID invoicePayableAt, UUID id);

  @Modifying
  @Query("UPDATE booking SET place_of_issue_id = :placeOfIssue where id = :id")
  Mono<Boolean> setPlaceOfIssueIDFor(UUID placeOfIssue, UUID id);

  @Modifying
  @Query(
      "UPDATE booking SET document_status = :documentStatus, updated_date_time = :updatedDateTime"
          + " where carrier_booking_request_reference = :carrierBookingRequestReference AND valid_until IS NULL")
  Mono<Boolean> updateDocumentStatusAndUpdatedDateTimeForCarrierBookingRequestReference(
      ShipmentEventTypeCode documentStatus,
      String carrierBookingRequestReference,
      OffsetDateTime updatedDateTime);

  @Deprecated
  @Query(
      "SELECT DISTINCT b.* FROM shipment s "
          + "JOIN booking b ON b.id = s.booking_id "
          + "WHERE s.carrier_booking_reference = :carrierBookingReference")
  Flux<Booking> findAllByCarrierBookingReference(String carrierBookingReference);

  @Query(
      "SELECT DISTINCT b.* FROM shipment s "
          + "JOIN booking b ON b.id = s.booking_id "
          + "WHERE s.carrier_booking_reference = :carrierBookingReference "
          + "ORDER BY b.valid_until NULLS FIRST LIMIT 1")
  Mono<Booking> findCarrierBookingReferenceAndValidUntilIsNull(
      String carrierBookingReference);

  @Query(
      "SELECT DISTINCT b.* FROM shipping_instruction si "
          + "JOIN consignment_item ci ON ci.shipping_instruction_id = si.id "
          + "JOIN shipment s ON s.id = ci.shipment_id "
          + "JOIN booking b ON b.id = s.booking_id "
          + "WHERE si.shipping_instruction_reference = :shippingInstructionReference")
  Flux<Booking> findAllByShippingInstructionReference(String shippingInstructionReference);
}
