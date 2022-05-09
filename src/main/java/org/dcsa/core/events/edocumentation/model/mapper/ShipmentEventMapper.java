package org.dcsa.core.events.edocumentation.model.mapper;

import org.dcsa.core.events.edocumentation.model.transferobject.BookingTO;
import org.dcsa.core.events.model.Booking;
import org.dcsa.core.events.model.ShipmentEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ShipmentEventMapper {


  @Mapping(target = "documentTypeCode", expression = "java(org.dcsa.core.events.model.enums.DocumentTypeCode.CBR)")
  @Mapping(expression = "java(org.dcsa.core.events.model.enums.EventClassifierCode.ACT)", target = "eventClassifierCode")
  @Mapping(source = "booking.id", target = "documentID")
  @Mapping(source = "booking.documentStatus", target = "shipmentEventTypeCode")
  @Mapping(source = "booking.carrierBookingRequestReference", target = "documentReference")
  @Mapping(source = "booking.updatedDateTime", target = "eventDateTime")
  @Mapping(expression = "java(java.time.OffsetDateTime.now())", target = "eventCreatedDateTime")
  @Mapping(source = "reason", target = "reason")
  ShipmentEvent shipmentEventFromBooking(Booking booking, String reason);

  @Mapping(target = "documentTypeCode", expression = "java(org.dcsa.core.events.model.enums.DocumentTypeCode.CBR)")
  @Mapping(expression = "java(org.dcsa.core.events.model.enums.EventClassifierCode.ACT)", target = "eventClassifierCode")
  @Mapping(source = "bookingID", target = "documentID")
  @Mapping(source = "bookingTO.documentStatus", target = "shipmentEventTypeCode")
  @Mapping(source = "bookingTO.carrierBookingRequestReference", target = "documentReference")
  @Mapping(source = "bookingTO.bookingRequestUpdatedDateTime", target = "eventDateTime")
  @Mapping(expression = "java(java.time.OffsetDateTime.now())", target = "eventCreatedDateTime")
  @Mapping(source = "reason", target = "reason")
  ShipmentEvent shipmentEventFromBookingTO(BookingTO bookingTO, UUID bookingID, String reason);
}
