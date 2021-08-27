package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.enums.TransportEventTypeCode;
import org.dcsa.core.events.model.transferobjects.DocumentReferenceTO;
import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Table("transport_event")
@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("TRANSPORT")
public class TransportEvent extends Event implements TransportCallBasedEvent {

    @Column("transport_event_type_code")
    private TransportEventTypeCode transportEventTypeCode;

    @Column("delay_reason_code")
    private String delayReasonCode;

    @Column("change_remark")
    private String changeRemark;

    @Column("transport_call_id")
    private String transportCallID;

    @Transient
    private TransportCallTO transportCall;

    @Transient
    private List<DocumentReferenceTO> documentReferences;

    @Transient
    private List<Reference> references;

    @JsonProperty("vesselScheduleChangeRemark")
    @Deprecated
    public String getVesselScheduleChangeRemark() {
        return changeRemark;
    }

    @JsonProperty("eventTypeCode")
    @Deprecated
    public TransportEventTypeCode getEventTypeCode() {
        return transportEventTypeCode;
    }
}
