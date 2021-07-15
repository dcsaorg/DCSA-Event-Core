package org.dcsa.core.events.service;

import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.TransportCall;
import org.dcsa.core.events.model.transferobjects.DocumentReferenceTO;
import org.dcsa.core.service.BaseService;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TransportCallService extends BaseService<TransportCall, String> {

    Mono<List<DocumentReferenceTO>> findDocumentReferencesForTransportCallID(String transportCallID);

    Mono<List<Reference>> findReferencesForTransportCallID(String transportCallID);
}
