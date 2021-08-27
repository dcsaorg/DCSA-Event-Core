package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.TransportDocumentType;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;

public interface TransportDocumentTypeRepository
    extends ExtendedRepository<TransportDocumentType, String> {

  @Query(
      "SELECT DISTINCT tdt.transport_document_type_code FROM transport_document_type tdt "
          + "JOIN shipping_instruction si "
          + "ON si.transport_document_type = tdt.transport_document_type_code "
          + "LEFT JOIN cargo_item ci "
          + "ON ci.shipping_instruction_id = si.id "
          + "LEFT JOIN \"references\" r "
          + "ON r.shipping_instruction_id = si.id "
          + "JOIN shipment s "
          + "ON (ci.shipment_id = s.id OR r.shipment_id = s.id) "
          + "WHERE s.carrier_booking_reference = :carrierBookingRef")
  Flux<String> findCodesByCarrierBookingRef(String carrierBookingRef);

  @Query(
      "SELECT DISTINCT tdt.transport_document_type_code FROM transport_document_type tdt "
          + "JOIN shipping_instruction si "
          + "ON si.transport_document_type = tdt.transport_document_type_code "
          + "WHERE si.id = :shippingInstructionID")
  Flux<String> findCodesByShippingInstructionID(String shippingInstructionID);

  @Query(
      "SELECT DISTINCT si.transport_document_type_code FROM shipping_instruction si "
          + "JOIN transport_document td "
          + "ON td.shipping_instruction_id = si.id "
          + "WHERE td.transport_document_reference = :transportDocumentReference")
  Flux<String> findCodesByTransportDocumentReference(String transportDocumentReference);

  @Query(
      "SELECT DISTINCT si.transport_document_type_code FROM shipping_instruction si "
          + "LEFT JOIN cargo_item ci ON si.id = ci.shipping_instruction_id "
          + "LEFT JOIN \"references\" r ON si.id = r.shipping_instruction_id "
          + "JOIN shipment_transport st ON (st.shipment_id = ci.shipment_id OR st.shipment_id = r.shipment_id) "
          + "JOIN transport t WHERE t.load_transport_call_id = :transportCallID")
  Flux<String> findCodesByTransportCallID(String transportCallID);
}
