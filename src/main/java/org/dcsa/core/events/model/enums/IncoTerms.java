package org.dcsa.core.events.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IncoTerms {
  FCA("Free Carrier"),
  FOB("Free on Board");

  private final String value;
}
