package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.enums.EmptyIndicatorCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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

}
