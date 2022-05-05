package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.enums.DocumentReferenceType;
import org.dcsa.core.events.model.enums.DocumentTypeCode;
import org.dcsa.core.events.model.transferobjects.DocumentReferenceTO;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

// There is no ID on the DocumentReference, so the Object was used as a placeholder
// - the repository is only here for the custom queries, so the ID type is irrelevant.
public interface DocumentReferenceRepository extends R2dbcRepository<DocumentReferenceRepository, Object> {


  @Query("SELECT edr.document_reference_type, edr.document_reference_value"
     + " FROM event_document_reference edr"
     + " WHERE edr.link_type = 'TC_ID'"
     + "   AND edr.transport_call_id = :transportCallID"
     + "   AND edr.document_reference_type IN (:documentReferenceTypes)"
  )
  Flux<DocumentReferenceTO> findAllDocumentReferenceByTransportCallID(String transportCallID, List<DocumentReferenceType> documentReferenceTypes);


  @Query("SELECT edr.document_reference_type, edr.document_reference_value"
    + " FROM event_document_reference edr"
    + " WHERE edr.link_type = :documentTypeCode"
    + "   AND edr.document_id = :documentID"
    + "   AND edr.document_reference_type IN (:documentReferenceTypes)"
  )
  Flux<DocumentReferenceTO> findAllDocumentReferenceByDocumentTypeCodeAndDocumentID(DocumentTypeCode documentTypeCode, UUID documentID, List<DocumentReferenceType> documentReferenceTypes);
}
