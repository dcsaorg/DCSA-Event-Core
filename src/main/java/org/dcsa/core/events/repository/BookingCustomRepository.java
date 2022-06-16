package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Booking;
import org.dcsa.core.events.model.enums.ShipmentEventTypeCode;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingCustomRepository {

  Flux<Booking> findAllByDocumentStatus(
      ShipmentEventTypeCode documentStatus, Pageable pageable);
  Mono<Long> countAllByDocumentStatus(
    ShipmentEventTypeCode documentStatus);
}
