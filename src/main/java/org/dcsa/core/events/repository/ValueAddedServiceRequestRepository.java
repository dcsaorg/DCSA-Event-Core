package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.ValueAddedServiceRequest;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ValueAddedServiceRequestRepository
    extends ExtendedRepository<ValueAddedServiceRequest, UUID> {

  Flux<ValueAddedServiceRequest> findByBookingID(UUID bookingID);
}
