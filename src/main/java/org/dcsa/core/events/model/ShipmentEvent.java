package org.dcsa.core.events.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.enums.DocumentTypeCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import javax.validation.constraints.Size;
import java.util.UUID;

@Table("shipment_event")
@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("SHIPMENT")
public class ShipmentEvent extends Event {

    @Column("document_type_code")
    private DocumentTypeCode documentTypeCode;

    @Column("document_id")
    @Size(max = 100)
    private String documentID;

    @Column("reason")
    @Size(max = 100)
    private String reason;

    @JsonProperty("shipmentID")
    public UUID getShipmentID() {
        if (documentTypeCode == DocumentTypeCode.SHI) {
            return UUID.fromString(documentID);
        }
        return null;
    }

    @JsonProperty("shipmentInformationTypeCode")
    public DocumentTypeCode getShipmentInformationTypeCode() {
        if (documentTypeCode == DocumentTypeCode.BKG) {
            return DocumentTypeCode.BOK;
        }
        return documentTypeCode;
    }
}
