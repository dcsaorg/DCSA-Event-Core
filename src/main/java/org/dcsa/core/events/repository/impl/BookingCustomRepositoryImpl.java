package org.dcsa.core.events.repository.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Booking;
import org.dcsa.core.events.model.enums.ShipmentEventTypeCode;
import org.dcsa.core.events.repository.BookingCustomRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@RequiredArgsConstructor
public class BookingCustomRepositoryImpl implements BookingCustomRepository {

  private final R2dbcEntityTemplate r2dbcEntityTemplate;

  @Override
  public Flux<Booking> findAllByDocumentStatus(ShipmentEventTypeCode documentStatus, Pageable pageable) {

    Criteria criteria = Criteria.from(getCriteriaHasDocumentStatus(documentStatus));

    return r2dbcEntityTemplate
        .select(Booking.class)
        .matching(
            query(criteria)
                .sort(pageable.getSort())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset()))
        .all();
  }

  @Override
  public Mono<Long> countAllByDocumentStatus(ShipmentEventTypeCode documentStatus) {

    Criteria criteria = getCriteriaHasDocumentStatus(documentStatus);

    return r2dbcEntityTemplate.select(Booking.class).matching(query(criteria)).count();
  }

  protected Criteria getCriteriaHasCarrierBookingRequestReference(
      String carrierBookingRequestReference) {
    Criteria criteria = Criteria.empty();
    if (carrierBookingRequestReference != null) {
      criteria = where("carrier_booking_request_reference").is(carrierBookingRequestReference);
    }
    return criteria;
  }

  protected Criteria getCriteriaHasBookingID(UUID bookingID) {
    Criteria criteria = Criteria.empty();
    if (bookingID != null) {
      criteria = where("id").is(bookingID);
    }
    return criteria;
  }

  protected Criteria getCriteriaHasDocumentStatus(ShipmentEventTypeCode documentStatus) {
    Criteria criteria = Criteria.empty();
    if (documentStatus != null) {
      criteria = where("document_status").is(documentStatus);
    }
    return criteria;
  }
}
