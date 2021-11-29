package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Voyage;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Repository
public interface VoyageRepository extends ExtendedRepository<Voyage, UUID> {

  @Query("SELECT DISTINCT v.carrier_voyage_number FROM voyage v " +
          "JOIN transport_call tc " +
          " ON tc.import_voyage_id = v.id OR tc.export_voyage_id = v.id " +
          "JOIN transport t " +
          " ON t.load_transport_call_id = tc.id " +
          "JOIN shipment_transport st " +
          " ON st.transport_id = t.id " +
          "JOIN shipment s " +
          " ON s.id = st.shipment_id " +
          "WHERE s.carrier_booking_reference = :carrierBookingRef")
  Flux<String> findCarrierVoyageNumbersByCarrierBookingRef(String carrierBookingRef);

  @Query("SELECT DISTINCT v.carrier_voyage_number FROM voyage v " +
          "JOIN transport_call tc " +
          " ON tc.import_voyage_id = v.id OR tc.export_voyage_id = v.id " +
          "JOIN transport t " +
          " ON t.load_transport_call_id = tc.id " +
          "JOIN shipment_transport st " +
          " ON st.transport_id = t.id " +
          "JOIN cargo_item ci " +
          " ON ci.shipment_id = st.shipment_id " +
          "LEFT JOIN \"reference\" r" +
          " ON r.shipment_id = st.shipment_id" +
          "WHERE (ci.shipping_instruction_id = :shippingInstructionID OR references.shipping_instruction_id = :shippingInstructionID)")
  Flux<String> findCarrierVoyageNumbersByShippingInstructionID(String shippingInstructionID);

  @Query("SELECT DISTINCT v.carrier_voyage_number FROM voyage v " +
          "JOIN transport_call tc " +
          " ON tc.import_voyage_id = v.id OR tc.export_voyage_id = v.id " +
          "JOIN transport t " +
          " ON t.load_transport_call_id = tc.id " +
          "JOIN shipment_transport st " +
          " ON st.transport_id = t.id " +
          "JOIN cargo_item ci " +
          " ON ci.shipment_id = st.shipment_id " +
          "JOIN transport_document td " +
          " ON td.shipping_instruction_id = ci.shipping_instruction_id " +
          "WHERE td.transport_document_reference = :transportDocumentRef")
  Flux<String> findCarrierVoyageNumbersByTransportDocumentRef(String transportDocumentRef);

  Mono<Voyage> findByCarrierVoyageNumberAndServiceID(String carrierVoyageNumber, UUID serviceID);
}
