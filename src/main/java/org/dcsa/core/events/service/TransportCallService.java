package org.dcsa.core.events.service;

import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.Seal;
import org.dcsa.core.events.model.TransportCall;
import org.dcsa.core.events.model.transferobjects.DocumentReferenceTO;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TransportCallService {

    Mono<List<DocumentReferenceTO>> findDocumentReferencesForTransportCallID(String transportCallID);

    Mono<List<Reference>> findReferencesForTransportCallID(String transportCallID);

    Mono<List<Seal>> findSealsForTransportCallIDAndEquipmentReference(String transportCallID, String equipmentReference);

    Mono<TransportCall> create(TransportCall transportCall);
}
