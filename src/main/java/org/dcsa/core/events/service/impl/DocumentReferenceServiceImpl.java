package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Event;
import org.dcsa.core.events.model.ShipmentEvent;
import org.dcsa.core.events.model.TransportCallBasedEvent;
import org.dcsa.core.events.model.enums.DocumentReferenceType;
import org.dcsa.core.events.model.transferobjects.DocumentReferenceTO;
import org.dcsa.core.events.repository.DocumentReferenceRepository;
import org.dcsa.core.events.service.DocumentReferenceService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentReferenceServiceImpl implements DocumentReferenceService {
  private final DocumentReferenceRepository documentReferenceRepository;

  private static final List<DocumentReferenceType> ALL_DOCUMENT_REFERENCE_TYPES = List.copyOf(EnumSet.allOf(DocumentReferenceType.class));

  @Override
  public Mono<List<DocumentReferenceTO>> findAllDocumentReferencesForEvent(Event e) {
    Flux<DocumentReferenceTO> documentReferenceTOFlux;
    if (e instanceof ShipmentEvent) {
      ShipmentEvent se = (ShipmentEvent) e;
      documentReferenceTOFlux = documentReferenceRepository.findAllDocumentReferenceByDocumentTypeCodeAndDocumentID(se.getDocumentTypeCode(), se.getDocumentID(), ALL_DOCUMENT_REFERENCE_TYPES);
    } else if (e instanceof TransportCallBasedEvent) {
      TransportCallBasedEvent tce = (TransportCallBasedEvent)e;
      documentReferenceTOFlux = documentReferenceRepository.findAllDocumentReferenceByTransportCallID(tce.getTransportCallID(), ALL_DOCUMENT_REFERENCE_TYPES);
    } else {
      return Mono.just(Collections.emptyList());
    }
    return documentReferenceTOFlux.collectList();
  }
}
