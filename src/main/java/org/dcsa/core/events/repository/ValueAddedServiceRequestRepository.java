package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.ValueAddedServiceRequest;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Size;
import java.util.UUID;

@Repository
public interface ValueAddedServiceRequestRepository
    extends ReactiveCrudRepository<ValueAddedServiceRequest, UUID> {

  Flux<ValueAddedServiceRequest> findByBookingID(UUID bookingID);

  // TODO: use shippingInstructionId when implemented
  Flux<ValueAddedServiceRequest> findByShippingInstructionID(
      @Size(max = 100) String shippingInstructionID);

  Mono<Void> deleteByBookingID(UUID bookingID);

  // TODO: use shippingInstructionId when implemented
  Mono<Void> deleteByShippingInstructionID(@Size(max = 100) String shippingInstructionID);
}
