package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Transport;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface TransportRepository extends ReactiveCrudRepository<Transport, UUID> {

  @Query(
      "SELECT DISTINCT t.vessel_imo_number FROM transport t "
          + "JOIN shipment_transport st "
          + "ON st.transport_id = t.id "
          + "JOIN shipment s "
          + "ON s.id = st.shipment_id "
          + "WHERE s.carrier_booking_reference = :carrierBookingRef")
  Flux<String> findVesselIMONumbersByCarrierBookingRef(String carrierBookingRef);

  @Query(
      "SELECT DISTINCT t.vessel_imo_number FROM transport t "
          + "JOIN shipment_transport st "
          + " ON st.transport_id = t.id "
          + "JOIN consignment_item ci "
          + " ON st.shipment_id = ci.shipment_id "
          + "LEFT JOIN reference r "
          + "ON r.shipment_id = st.shipment_id "
          + "WHERE (ci.shipping_instruction_id = :shippingInstructionID OR r.shipping_instruction_id = :shippingInstructionID)")
  Flux<String> findVesselIMONumbersByShippingInstructionID(UUID shippingInstructionID);

  @Query(
      "SELECT DISTINCT t.vessel_imo_number FROM transport t "
          + "JOIN shipment_transport st "
          + " ON st.transport_id = t.id "
          + "JOIN consignment_item ci "
          + " ON st.shipment_id = ci.shipment_id "
          + "LEFT JOIN reference r "
          + "ON r.shipment_id = st.shipment_id "
          + "JOIN transport_document td "
          + "ON (td.shipping_instruction_id = ci.shipping_instruction_id OR td.shipping_instruction_id = r.shipping_instruction_id) "
          + "WHERE td.transport_document_reference = :transportDocumentRef")
  Flux<String> findVesselIMONumbersByTransportDocumentRef(String transportDocumentRef);
}
