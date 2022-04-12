package org.dcsa.core.events.model;


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

    @Column("document_id")
    @Size(max = 100)
    private String documentID;

    @Column("reason")
    @Size(max = 250)
    private String reason;

    @Transient
    private List<Reference> references;
}
