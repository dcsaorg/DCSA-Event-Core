package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.ShipmentEvent;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ShipmentEventRepository extends ExtendedRepository<ShipmentEvent, UUID> {

  @Query("SELECT * FROM shipment_event a WHERE (:eventType IS NULL or a.event_type =:eventType)")
  Flux<ShipmentEvent> findShipmentEventsByFilters(@Param("eventType") EventType eventType);

  @Query(
      "SELECT DISTINCT td.transport_document_reference from transport_document td "
          + "JOIN shipping_instruction si ON shipping_instruction_id = si.id "
          + "JOIN cargo_item ci ON si.id = ci.shipping_instruction_id "
          + "JOIN shipment s ON ci .shipment_id = s.id "
          + "WHERE s.carrier_booking_reference = :carrierBookingRef")
  Flux<String> findTransportDocumentRefsByCarrierBookingRef(String carrierBookingRef);

  @Query(
      "SELECT DISTINCT td.transport_document_reference FROM transport_document td "
          + "JOIN shipping_instruction si ON td.shipping_instruction_id  = si .id "
          + "JOIN cargo_item ci ON si.id = ci.shipping_instruction_id "
          + "WHERE ci.shipping_instruction_id = :shippingInstructionID")
  Flux<String> findTransportDocumentRefsByShippingInstructionID(String shippingInstructionID);
}
