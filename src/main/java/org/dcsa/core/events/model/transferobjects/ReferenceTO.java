package org.dcsa.core.events.model.transferobjects;

import lombok.Data;
import org.dcsa.core.events.model.enums.ReferenceTypeCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ReferenceTO {

  @NotNull(message = "ReferenceTypeCode is required.")
  private ReferenceTypeCode referenceType;

  @NotBlank(message = "ReferenceValue is required.")
  @Size(max = 100, message = "ReferenceValue has max size of 100.")
  private String referenceValue;
}
