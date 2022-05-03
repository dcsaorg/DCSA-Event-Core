package org.dcsa.core.events.edocumentation.model.mapper;

import org.dcsa.core.events.edocumentation.model.transferobject.BookingResponseTO;
import org.dcsa.core.events.edocumentation.model.transferobject.BookingTO;
import org.dcsa.core.events.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface BookingMapper {
  @Mapping(source = "invoicePayableAt", target = "invoicePayableAt.id")
  @Mapping(source = "placeOfIssueID", target = "placeOfIssue.id")
  @Mapping(source = "communicationChannelCode", target = "communicationChannel")
  @Mapping(source = "updatedDateTime", target = "bookingRequestUpdatedDateTime")
  @Mapping(source = "bookingRequestDateTime", target = "bookingRequestCreatedDateTime")
  BookingTO bookingToDTO(Booking booking);

  @Mapping(source = "invoicePayableAt", target = "invoicePayableAt", ignore = true)
  @Mapping(source = "communicationChannel", target = "communicationChannelCode")
  @Mapping(source = "bookingRequestUpdatedDateTime", target = "updatedDateTime")
  @Mapping(source = "bookingRequestCreatedDateTime", target = "bookingRequestDateTime")
  Booking dtoToBooking(BookingTO bookingTO);

  @Mapping(source = "bookingTO.invoicePayableAt", target = "invoicePayableAt", ignore = true)
  @Mapping(source = "bookingTO.communicationChannel", target = "communicationChannelCode")
  @Mapping(source = "bookingTO.bookingRequestUpdatedDateTime", target = "updatedDateTime")
  @Mapping(source = "bookingTO.bookingRequestCreatedDateTime", target = "bookingRequestDateTime")
  @Mapping(source = "bookingId", target = "id")
  Booking dtoToBookingWithBookingId(BookingTO bookingTO, UUID bookingId);

  @Mapping(source = "updatedDateTime", target = "bookingRequestUpdatedDateTime")
  @Mapping(source = "bookingRequestDateTime", target = "bookingRequestCreatedDateTime")
  BookingResponseTO bookingToBookingResponseTO(Booking booking);

  BookingResponseTO dtoToBookingResponseTO(BookingTO bookingTO);
}
