package org.dcsa.core.events.service;

import org.dcsa.core.events.model.Reference;
import org.dcsa.core.service.ExtendedBaseService;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ReferenceService extends ExtendedBaseService<Reference, UUID> {

  Flux<Reference> findByShippingInstructionID(String shippingInstructionID);

  Flux<Reference> findByShipmentID(UUID shipmentID);

  Flux<Reference> findByTransportDocumentReference(String transportDocumentReference);
}
