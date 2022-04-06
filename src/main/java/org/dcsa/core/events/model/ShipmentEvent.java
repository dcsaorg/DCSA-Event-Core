package org.dcsa.core.events.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.enums.DocumentTypeCode;
import org.dcsa.core.events.model.enums.ShipmentEventTypeCode;
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
//    @JsonProperty("foo")
    private UUID documentID;

    @Column("document_reference")
    @Size(max = 100)
//    @JsonProperty("documentID")
    private String documentReference;

    @Column("reason")
    @Size(max = 250)
    private String reason;


    @JsonProperty("shipmentID")
    @Deprecated
    public UUID getShipmentID() {
        if (documentTypeCode == DocumentTypeCode.SHI) {
            return documentID;
        }
        return null;
    }

    @JsonProperty("shipmentInformationTypeCode")
    @Deprecated
    public DocumentTypeCode getShipmentInformationTypeCode() {
        if (documentTypeCode == DocumentTypeCode.BKG) {
            return DocumentTypeCode.BOK;
        }
        return documentTypeCode;
    }


    @Transient
    private List<Reference> references;
}
