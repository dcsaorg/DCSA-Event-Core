package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Booking;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BookingRepository extends ExtendedRepository<Booking, String> {

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
          + "JOIN transport t ON st.transport_id = t.id"
          + "WHERE t.load_transport_call_id = :transportCallID")
  Flux<String> findCarrierBookingRefsByTransportCallID(String transportCallID);
}