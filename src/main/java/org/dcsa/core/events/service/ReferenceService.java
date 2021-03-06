package org.dcsa.core.events.service;

import org.dcsa.core.events.model.transferobjects.ReferenceTO;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface ReferenceService {

  Mono<List<ReferenceTO>> createReferencesByBookingIDAndTOs(
      UUID bookingID, List<ReferenceTO> references);

  Mono<List<ReferenceTO>> createReferencesByShippingInstructionIdAndTOs(
      UUID shippingInstructionReference, List<ReferenceTO> references);

  Mono<List<ReferenceTO>> createReferencesByShippingInstructionReferenceAndConsignmentIdAndTOs(
      UUID shippingInstructionReference, UUID consignmentId, List<ReferenceTO> references);

  Mono<List<ReferenceTO>> findByBookingID(UUID bookingID);

  Mono<List<ReferenceTO>> findByShippingInstructionID(UUID shippingInstructionID);

  Mono<List<ReferenceTO>> resolveReferencesForBookingID(
      List<ReferenceTO> references, UUID bookingID);

  Mono<List<ReferenceTO>> resolveReferencesForShippingInstructionReference(
      List<ReferenceTO> references, UUID shippingInstructionReference);
}
