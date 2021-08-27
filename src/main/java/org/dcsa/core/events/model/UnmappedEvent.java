package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Table("unmapped_event_queue")
public class UnmappedEvent implements Persistable<UUID> {

  @Id
  @Column("event_id")
  private UUID eventID;

  @Column("enqueued_at_date_time")
  private OffsetDateTime enqueuedAtDateTime;

  @Transient @JsonIgnore private boolean isNewRecord;

  @Override
  @JsonIgnore
  public UUID getId() {
    return this.eventID;
  }

  @Override
  @JsonIgnore
  public boolean isNew() {
    return this.isNewRecord || this.getId() == null;
  }
}
