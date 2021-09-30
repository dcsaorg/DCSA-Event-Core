package org.dcsa.core.events.model.enums;

import lombok.Getter;

public enum PortCallServiceTypeCode {
    PILO(null, "Pilotage"),
    MOOR(null, "Mooring"),  /* Timestamp name not confirmed (not mentioned in JIT 1.1) */
    CRGO(FacilityTypeCode.BRTH, "Cargo Ops"),
    TOWG(null,"Towage"),
    BUNK(FacilityTypeCode.BRTH, "Bunkering"),
    WSDP(null,"Waste disposal"),  /* Timestamp name not confirmed (not mentioned in JIT 1.1) */
    FAST(null,"All Fast"),  /* NB: Does not follow the "XTY Name" pattern */
    GWAY(null,"Gangway Down and Safe"),  /* NB: Does not follow the "XTY Name" pattern */
    SAFE(null,"Vessel Readiness for cargo operations"),  /* NB: Does not follow the "XTY Name" pattern */
    LASH(FacilityTypeCode.BRTH, "Lashing"),
    ;

    @Getter
    private final FacilityTypeCode expectedFacilityTypeCode;
    @Getter
    private final String timestampTypeNameSuffix;

    PortCallServiceTypeCode(FacilityTypeCode expectedFacilityTypeCode, String timestampTypeNameSuffix) {
        this.expectedFacilityTypeCode = expectedFacilityTypeCode;
        this.timestampTypeNameSuffix = timestampTypeNameSuffix;
    }

}
