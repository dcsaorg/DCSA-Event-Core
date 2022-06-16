package org.dcsa.core.events.service;

import org.dcsa.core.events.model.transferobjects.DocumentPartyTO;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface DocumentPartyService {

  Mono<List<DocumentPartyTO>> createDocumentPartiesByBookingID(
      UUID bookingID, List<DocumentPartyTO> documentParties);

  Mono<List<DocumentPartyTO>> createDocumentPartiesByShippingInstructionID(
      UUID shippingInstructionID, List<DocumentPartyTO> documentParties);

  Mono<List<DocumentPartyTO>> fetchDocumentPartiesByBookingID(UUID bookingID);

  Mono<List<DocumentPartyTO>> fetchDocumentPartiesByByShippingInstructionID(
      UUID shippingInstructionReference);

  Mono<List<DocumentPartyTO>> resolveDocumentPartiesForShippingInstructionID(
      UUID shippingInstructionID, List<DocumentPartyTO> documentPartyTOs);
}
