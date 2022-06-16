package org.dcsa.core.events.edocumentation.model.transferobject;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public
class CarrierClauseTO {
  @NotNull(message = "ClauseContent is required.")
  private String clauseContent;
}
