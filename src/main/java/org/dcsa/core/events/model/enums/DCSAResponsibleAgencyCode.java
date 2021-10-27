package org.dcsa.core.events.model.enums;

import java.util.HashMap;
import java.util.Map;

public enum DCSAResponsibleAgencyCode {
  ISO("5"),
  UNECE("6"),
  LLOYD("11"),
  BIC("20"),
  IMO("54"),
  SCAC("182"),
  ITIGG("274"),
  ITU("296"),
  SMDG("306"),
  EXIS("399"),
  FMC(""),
  CBSA(""),
  ZZZ("zzz");

  private static final Map<String, DCSAResponsibleAgencyCode> LEGACY_CODES_2_DCSA_CODE;

  private final String code;

  DCSAResponsibleAgencyCode(String code) {
    this.code = code;
  }

  public String getLegacyAgencyCode() {
    return this.code;
  }

  public static DCSAResponsibleAgencyCode legacyCode2DCSACode(String legacyCode) {
    DCSAResponsibleAgencyCode dcsaCode = LEGACY_CODES_2_DCSA_CODE.get(legacyCode);
    if (dcsaCode == null) {
      throw new IllegalArgumentException("Invalid code list responsible agency code");
    }
    return dcsaCode;
  }

  public static void ensureIsValidLegacyCode(String legacyCode) {
    legacyCode2DCSACode(legacyCode);
  }

  static {
    LEGACY_CODES_2_DCSA_CODE = new HashMap<>();
    for (DCSAResponsibleAgencyCode dcsaCode : DCSAResponsibleAgencyCode.values()) {
      if (dcsaCode.code.equals("")) {
        continue;
      }
      LEGACY_CODES_2_DCSA_CODE.put(dcsaCode.code, dcsaCode);
    }
  }
}
