package org.dcsa.core.events.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DocumentStatus {
  RECE("Received"),
  PENU("Pending Update"),
  REJE("Rejected"),
  CONF("Confirmed"),
  PENC("Pending Confirmation"),
  CANC("Cancelled");

  private final String value;
}
