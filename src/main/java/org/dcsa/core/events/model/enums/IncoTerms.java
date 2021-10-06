package org.dcsa.core.events.model.enums;

public enum IncoTerms {
  FCA("Free Carrier"),
  FOB("Free on Board");

  private final String value;

  IncoTerms(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
