package org.dcsa.core.events.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DocumentStatus {
  RECE("Received"),
  DRFT("Drafted"),
  PENA("Pending Approval"),
  PENU("Pending Update"),
  REJE("Rejected"),
  APPR("Approved"),
  ISSU("Issued"),
  SURR("Surrendered"),
  SUBM("Submitted"),
  VOID("Void");

  private final String value;
}
