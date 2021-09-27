package org.dcsa.core.events.model.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.model.AuditBase;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractEventSubscription extends AuditBase {

  @Id
  @Column("subscription_id")
  protected UUID subscriptionID;

  @NotBlank
  @Column("callback_url")
  protected String callbackUrl;

  @Column("secret")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  // Jackson encodes this in base64 by default
  protected byte[] secret;

  public List<EventType> getEventType() {
    return Collections.emptyList();
  }
}
