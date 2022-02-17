package org.dcsa.core.events.edocumentation.repository;

import org.dcsa.core.events.model.CarrierClause;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface CarrierClauseRepository extends ReactiveCrudRepository<CarrierClause, UUID> {

  @Query("select cc.* "
		+ "from dcsa_im_v3_0.carrier_clauses cc "
		+ "join dcsa_im_v3_0.shipment_carrier_clauses scc on scc.carrier_clause_id = cc.id "
		+ "where scc.shipment_id  = :shipmentID")
  Flux<CarrierClause> fetchAllByShipmentID(UUID shipmentID);

	@Query("select cc.* "
		+ "from carrier_clauses cc "
		+ "join shipment_carrier_clauses scc on scc.carrier_clause_id = cc.id "
		+ "where scc.transport_document_reference = :transportDocumentReference")
	Flux<CarrierClause> fetchAllByTransportDocumentReference(String transportDocumentReference);

}
