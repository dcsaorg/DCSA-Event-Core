package org.dcsa.core.events.service;

import org.dcsa.core.events.model.Event;
import org.dcsa.core.events.model.transferobjects.DocumentReferenceTO;
import reactor.core.publisher.Mono;

import java.util.List;

public interface DocumentReferenceService {

  Mono<List<DocumentReferenceTO>> findAllDocumentReferencesForEvent(Event e);
}
