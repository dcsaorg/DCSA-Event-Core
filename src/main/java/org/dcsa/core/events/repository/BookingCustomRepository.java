package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Booking;
import org.dcsa.core.events.model.enums.DocumentStatus;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BookingCustomRepository {

  Flux<Booking> findAllByDocumentStatus(
      DocumentStatus documentStatus, Pageable pageable);
  Flux<Booking> findAllByBookingIDAndDocumentStatus(UUID bookingID, DocumentStatus documentStatus, Pageable pageable);
  public Mono<Long> countAllByCarrierBookingReferenceAndDocumentStatus(
    String carrierBookingRequestReference,DocumentStatus documentStatus);
}
