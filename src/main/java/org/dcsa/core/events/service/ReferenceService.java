package org.dcsa.core.events.service;

import org.dcsa.core.events.model.transferobjects.ReferenceTO;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface ReferenceService {

  Mono<List<ReferenceTO>> createReferencesByBookingIDAndTOs(
      UUID bookingID, List<ReferenceTO> references);

  Mono<List<ReferenceTO>> createReferencesByShippingInstructionIDAndTOs(
      String shippingInstructionID, List<ReferenceTO> references);

  Mono<List<ReferenceTO>> findByBookingID(UUID bookingID);

  Mono<List<ReferenceTO>> findByShippingInstructionID(String shippingInstructionID);

  Mono<List<ReferenceTO>> findByCargoItemID(UUID cargoItemID);

  Mono<List<ReferenceTO>> resolveReferencesForBookingID(
      List<ReferenceTO> references, UUID bookingID);

  Mono<List<ReferenceTO>> resolveReferencesForShippingInstructionID(
      List<ReferenceTO> references, String shippingInstructionID);
}
