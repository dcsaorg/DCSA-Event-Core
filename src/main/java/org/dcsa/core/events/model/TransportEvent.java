package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.enums.DocumentReferenceType;
import org.dcsa.core.events.model.transferobjects.DocumentReferenceTO;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Table("transport_event")
@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("TRANSPORT")
public class TransportEvent extends Event {

    @Column("transport_event_type_code")
    private String transportEventTypeCode;

    @Column("delay_reason_code")
    private String delayReasonCode;

    @Column("change_remark")
    private String changeRemark;

    @Column("transport_call_id")
    private String transportCallID;

    @Transient
    private TransportCall transportCall;

    @Transient
    private List<DocumentReferenceTO> documentReferences;

    @Transient
    private List<Reference> references;
}
