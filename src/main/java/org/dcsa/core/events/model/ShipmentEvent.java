package org.dcsa.core.events.model;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.enums.DocumentTypeCode;
import org.dcsa.core.events.model.enums.ShipmentEventTypeCode;
import org.dcsa.core.events.model.transferobjects.DocumentReferenceTO;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@Table("shipment_event")
@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("SHIPMENT")
public class ShipmentEvent extends Event {

    @Column("shipment_event_type_code")
    private ShipmentEventTypeCode shipmentEventTypeCode;

    @Column("document_type_code")
    private DocumentTypeCode documentTypeCode;

    //Historically the documentID field contained the references and not the ID.
    //This is now changed, so the ShipmentEvent documentID field contains the ID of an entity (e.g. shipment or transport document)
    //The reference is now moved to the documentReference field. However, the JSON payload in the API need to preserve backwardscompatibility.
    //Hence, the documentID field (now containing the ID) is not provided in the jsonpayload and the documentReference field is marshalled with the JsonProperty documentID

    @Column("document_id")
    @Size(max = 100)
    @JsonIgnore
    private UUID documentID;

    @JsonIgnore
    @Column("document_reference")
    @Size(max = 100)
    private String documentReference;

    @JsonGetter("documentID")
    public String getDocumentReferenceAsDocumentID() {
      return documentReference;
    }

    @Column("reason")
    @Size(max = 250)
    private String reason;

    @Transient
    private List<Reference> references;

    @Transient
    private List<DocumentReferenceTO> documentReferences;
}
