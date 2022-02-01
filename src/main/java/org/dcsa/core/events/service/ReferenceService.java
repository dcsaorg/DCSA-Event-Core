package org.dcsa.core.events.service;

import org.dcsa.core.events.model.transferobjects.ReferenceTO;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReferenceService {

  Mono<Optional<List<ReferenceTO>>> createReferencesByBookingIDAndTOs(
      UUID bookingID, List<ReferenceTO> references);

  Mono<Optional<List<ReferenceTO>>> createReferencesByShippingInstructionIDAndTOs(
      String shippingInstructionID, List<ReferenceTO> references);

  Mono<Optional<List<ReferenceTO>>> findByBookingID(UUID bookingID);

  Mono<Optional<List<ReferenceTO>>> findByShippingInstructionID(String shippingInstructionID);

  Mono<Optional<List<ReferenceTO>>> resolveReferencesForBookingID(
      List<ReferenceTO> references, UUID bookingID);

  Mono<Optional<List<ReferenceTO>>> resolveReferencesForShippingInstructionID(
      List<ReferenceTO> references, String shippingInstructionID);
}
