package org.dcsa.core.events.service;

import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.transferobjects.ReferenceTO;
import org.dcsa.core.service.BaseService;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface ReferenceService extends BaseService<Reference, UUID> {

  Mono<List<ReferenceTO>> createReferencesByBookingIDAndTOs(
      UUID bookingID, List<ReferenceTO> references);

  Mono<List<ReferenceTO>> createReferencesByShippingInstructionIDAndTOs(
      String shippingInstructionID, List<ReferenceTO> references);

  Mono<List<ReferenceTO>> findByBookingID(UUID bookingID);

  Mono<List<ReferenceTO>> findByShippingInstructionID(String shippingInstructionID);

  Mono<List<ReferenceTO>> resolveReferencesForBookingID(
      List<ReferenceTO> references, UUID bookingID);

  Mono<List<ReferenceTO>> resolveReferencesForShippingInstructionID(
      List<ReferenceTO> references, String shippingInstructionID);
}
