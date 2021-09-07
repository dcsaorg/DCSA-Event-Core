package org.dcsa.core.events.model.enums;

import java.util.Arrays;

public enum CodeListResponsibleAgency {
  ISO("5"),
  UN_ECE("6"),
  LLROS("11"),
  BIC("20"),
  IMO("54"),
  SCAC("182"),
  ITIGG("274"),
  ITU("296"),
  SMDG("306"),
  EXIS("399"),
  MUTUALLY_DEFINED("zzz");

  private final String code;

  CodeListResponsibleAgency(String code) {
    this.code = code;
  }

  public String getCode() {
    return this.code;
  }

  public static void isValidCode(String code) {
    if (Arrays.stream(CodeListResponsibleAgency.values())
        .map(clra -> clra.code)
        .noneMatch(c -> c.equals(code))) {
      throw new IllegalArgumentException("Invalid code list responsible agency code");
    }
  }
}
