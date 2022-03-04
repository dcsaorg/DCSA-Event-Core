package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Service;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ServiceRepository extends ReactiveCrudRepository<Service, UUID> {

  @Query(
      "SELECT DISTINCT s.carrier_service_code FROM service s "
          + "JOIN voyage v "
          + " ON v.service_id = s.id "
          + "JOIN transport_call tc "
          + " ON tc.import_voyage_id = v.id OR tc.export_voyage_id = v.id "
          + "JOIN transport t "
          + " ON t.load_transport_call_id = tc.id "
          + "JOIN shipment_transport st "
          + " ON st.transport_id = t.id "
          + "JOIN shipment s "
          + " ON s.id = st.shipment_id "
          + "WHERE s.carrier_booking_reference = :carrierBookingRef")
  Flux<String> findCarrierServiceCodesByCarrierBookingRef(String carrierBookingRef);

  @Query(
      "SELECT DISTINCT s.carrier_service_code FROM service s "
          + "JOIN voyage v "
          + " ON v.service_id = s.id "
          + "JOIN transport_call tc "
          + " ON tc.import_voyage_id = v.id OR tc.export_voyage_id = v.id "
          + "JOIN transport t "
          + " ON t.load_transport_call_id = tc.id "
          + "JOIN shipment_transport st "
          + " ON st.transport_id = t.id "
          + "JOIN shipment_equipment se"
          + " ON st.shipment_id = se.shipment_id"
          + "JOIN cargo_item ci "
          + " ON ci.shipment_equipment_id = se.id "
          + "LEFT JOIN reference r"
          + " ON r.shipment_id = st.shipment_id"
          + "WHERE (ci.shipping_instruction_id = :shippingInstructionReference OR reference.shipping_instruction_id = :shippingInstructionReference)")
  Flux<String> findCarrierServiceCodesByShippingInstructionReference(String shippingInstructionReference);

  @Query(
      "SELECT DISTINCT s.carrier_service_code FROM service s "
          + "JOIN voyage v "
          + " ON v.service_id = s.id "
          + "JOIN transport_call tc "
          + " ON tc.import_voyage_id = v.id OR tc.export_voyage_id = v.id "
          + "JOIN transport t "
          + " ON t.load_transport_call_id = tc.id "
          + "JOIN shipment_transport st "
          + " ON st.transport_id = t.id "
          + "JOIN shipment_equipment se "
          + " ON se.shipment_id = st.shipment_id "
          + "JOIN cargo_item ci "
          + " ON ci.shipment_equipment_id = se.id "
          + "JOIN transport_document td "
          + " ON td.shipping_instruction_id = ci.shipping_instruction_id "
          + "WHERE td.transport_document_reference = :transportDocumentRef")
  Flux<String> findCarrierServiceCodesByTransportDocumentRef(String transportDocumentRef);

  Mono<Service> findByCarrierServiceCode(String carrierServiceCode);
}
