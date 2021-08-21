package org.dcsa.core.events.model.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.model.AuditBase;
import org.dcsa.core.validator.ValidationGroups;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractEventSubscription extends AuditBase {

  @Id
  @Column("subscription_id")
  protected UUID subscriptionID;

  @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
  @Column("callback_url")
  protected String callbackUrl;

  @Column("schedule_id")
  protected String scheduleID;

  @NotEmpty(groups = {ValidationGroups.Create.class})
  @Null(
      groups = {ValidationGroups.Update.class},
      message =
          "Please omit the \"secret\" attribute. If you want to change the"
              + " secret, please use the dedicated secret endpoint"
              + " (\"PUT .../event-subscriptions/{subscriptionId}"
              + "/secret\").")
  @Column("secret")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  // Jackson encodes this in base64 by default
  protected byte[] secret;
}
