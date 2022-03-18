package org.dcsa.core.events.service;

import org.dcsa.core.events.model.transferobjects.DocumentPartyTO;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface DocumentPartyService {

  Mono<List<DocumentPartyTO>> createDocumentPartiesByBookingID(
      UUID bookingID, List<DocumentPartyTO> documentParties);

  Mono<List<DocumentPartyTO>> createDocumentPartiesByShippingInstructionReference(
      String shippingInstructionReference, List<DocumentPartyTO> documentParties);

  Mono<List<DocumentPartyTO>> fetchDocumentPartiesByBookingID(UUID bookingID);

  Mono<List<DocumentPartyTO>> fetchDocumentPartiesByByShippingInstructionReference(
      String shippingInstructionReference);

  Mono<List<DocumentPartyTO>> resolveDocumentPartiesForShippingInstructionReference(
      String shippingInstructionReference, List<DocumentPartyTO> documentPartyTOs);
}
