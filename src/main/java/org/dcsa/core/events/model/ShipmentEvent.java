package org.dcsa.core.events.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.enums.DocumentTypeCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("shipment_event")
@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("SHIPMENT")
public class ShipmentEvent extends Event {

    @Column("document_type_code")
    private DocumentTypeCode documentTypeCode;

    @Column("shipment_id")
    private UUID shipmentID;

    @JsonProperty("shipmentInformationTypeCode")
    public DocumentTypeCode getShipmentInformationTypeCode() {
        if (documentTypeCode == DocumentTypeCode.BKG) {
            return DocumentTypeCode.BOK;
        }
        return documentTypeCode;
    }
}
