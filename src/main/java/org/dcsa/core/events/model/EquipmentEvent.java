package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.enums.EmptyIndicatorCode;
import org.dcsa.core.events.model.transferobjects.DocumentReferenceTO;
import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Table("equipment_event")
@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("EQUIPMENT")
public class EquipmentEvent extends Event {

    @Column("equipment_event_type_code")
    private String equipmentEventTypeCode;

    @Column("equipment_reference")
    private String equipmentReference;

    @Column("empty_indicator_code")
    private EmptyIndicatorCode emptyIndicatorCode;

    @Column("transport_call_id")
    private String transportCallID;

    @Transient
    private TransportCallTO transportCall;

    @Transient
    private List<DocumentReferenceTO> documentReferences;

    @Transient
    private List<Reference> references;

    @Transient
    private List<Seal> seals;
}
