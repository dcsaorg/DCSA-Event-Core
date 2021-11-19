package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Booking;
import org.dcsa.core.events.model.enums.DocumentStatus;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;

public interface BookingCustomRepository {

  Flux<Booking> findAllByCarrierBookingReferenceAndDocumentStatus(
      String carrierBookingRequestReference, DocumentStatus documentStatus, Pageable pageable);
}
