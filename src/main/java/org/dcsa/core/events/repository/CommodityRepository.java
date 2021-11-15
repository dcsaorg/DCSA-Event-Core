package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Commodity;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface CommodityRepository extends ExtendedRepository<Commodity, UUID> {
  Flux<Commodity> findByBookingID(UUID bookingID);
}
