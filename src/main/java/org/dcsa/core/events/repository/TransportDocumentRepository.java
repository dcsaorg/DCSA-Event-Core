package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.TransportDocument;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;

public interface TransportDocumentRepository extends ExtendedRepository<TransportDocument, String> {

  @Query(
      "SELECT DISTINCT td.transport_document_reference FROM transport_document td "
          + "LEFT JOIN cargo_item ci "
          + " ON ci.shipping_instruction_id = td.shipping_instruction_id "
          + "LEFT JOIN references r "
          + " ON r.shipping_instruction_id = td.shipping_instruction_id "
          + "JOIN shipment s "
          + " ON ci.shipment_id = s.id OR r.shipment_id = s.id "
          + "WHERE s.carrier_booking_reference = :carrierBookingRef")
  Flux<String> findTransportDocumentReferencesByCarrierBookingRef(String carrierBookingRef);

  Flux<TransportDocument> findDistinctTransportDocumentReferencesByShippingInstructionID(String shippingInstructionID);

  Flux<TransportDocument> findDistinctTransportDocumentReferencesByTransportDocumentReference(String transportDocumentReference);
}
