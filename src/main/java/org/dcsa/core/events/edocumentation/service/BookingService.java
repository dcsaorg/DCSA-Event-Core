package org.dcsa.core.events.edocumentation.service;

import org.dcsa.core.events.edocumentation.model.transferobject.BookingTO;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BookingService {
  Mono<BookingTO> fetchByBookingID(UUID bookingID);
}
