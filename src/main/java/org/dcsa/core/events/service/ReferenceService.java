package org.dcsa.core.events.service;

import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.transferobjects.ReferenceTO;
import org.dcsa.core.service.ExtendedBaseService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReferenceService extends ExtendedBaseService<Reference, UUID> {

  Flux<Reference> findByShippingInstructionID(String shippingInstructionID);

  Flux<Reference> findByShipmentID(UUID shipmentID);

  Flux<Reference> findByTransportDocumentReference(String transportDocumentReference);

  Mono<Optional<List<ReferenceTO>>> createReferencesByBookingIDAndTOs(UUID bookingID, String shippingInstructionID, List<ReferenceTO> references);
}
