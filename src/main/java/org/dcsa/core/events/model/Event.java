package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.enums.EventClassifierCode;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.model.AuditBase;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Table("aggregated_events")
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true,
        value = {"carrierBookingReference"})
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "eventType",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EquipmentEvent.class, name = "EQUIPMENT"),
        @JsonSubTypes.Type(value = TransportEvent.class, name = "TRANSPORT"),
        @JsonSubTypes.Type(value = ShipmentEvent.class, name = "SHIPMENT"),
        @JsonSubTypes.Type(value = OperationsEvent.class, name = "OPERATIONS")
})
public class Event extends AuditBase implements Persistable<UUID> {

    @Id
    @Column("event_id")
    private UUID eventID;

    @Column("event_type")
    private EventType eventType;

    @Column("event_date_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime eventDateTime;

    @Column("event_created_date_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @CreatedDate
    private OffsetDateTime eventCreatedDateTime;

    @Column("event_classifier_code")
    private EventClassifierCode eventClassifierCode;

    @Column("carrier_booking_reference")
    private String carrierBookingReference;

    @Transient
    @JsonIgnore
    private boolean isNewRecord;

    @Override
    public UUID getId() {
        return this.eventID;
    }

    @Override
    public boolean isNew() {
        return this.isNewRecord || this.getId() == null;
    }
}
