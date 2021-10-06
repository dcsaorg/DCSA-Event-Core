package org.dcsa.core.events.model.enums;

public enum PaymentTerm {
  PRE("Prepaid"),
  COL("Collect");

  private final String value;

  PaymentTerm(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
