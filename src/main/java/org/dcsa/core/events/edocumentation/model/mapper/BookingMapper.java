package org.dcsa.core.events.edocumentation.model.mapper;

import org.dcsa.core.events.edocumentation.model.transferobject.BookingResponseTO;
import org.dcsa.core.events.edocumentation.model.transferobject.BookingTO;
import org.dcsa.core.events.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {
  @Mapping(source = "invoicePayableAtId", target = "invoicePayableAt.id")
  @Mapping(source = "placeOfIssueId", target = "placeOfIssue.id")
  @Mapping(source = "communicationChannelCode", target = "communicationChannelCode")
  @Mapping(source = "updatedDateTime", target = "bookingRequestUpdatedDateTime")
  @Mapping(source = "bookingRequestDateTime", target = "bookingRequestCreatedDateTime")
  BookingTO bookingToDTO(Booking booking);

  @Mapping(source = "invoicePayableAt", target = "invoicePayableAtId", ignore = true)
  @Mapping(source = "communicationChannelCode", target = "communicationChannelCode")
  @Mapping(source = "bookingRequestUpdatedDateTime", target = "updatedDateTime")
  @Mapping(source = "bookingRequestCreatedDateTime", target = "bookingRequestDateTime")
  Booking dtoToBooking(BookingTO bookingTO);

  @Mapping(source = "updatedDateTime", target = "bookingRequestUpdatedDateTime")
  @Mapping(source = "bookingRequestDateTime", target = "bookingRequestCreatedDateTime")
  BookingResponseTO bookingToBookingResponseTO(Booking booking);

  BookingResponseTO dtoToBookingResponseTO(BookingTO bookingTO);
}
