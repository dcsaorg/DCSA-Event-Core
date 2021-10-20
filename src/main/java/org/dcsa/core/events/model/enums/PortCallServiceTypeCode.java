package org.dcsa.core.events.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Set;

import static org.dcsa.core.events.model.enums.EventClassifierCode.*;
import static org.dcsa.core.events.model.enums.FacilityTypeCode.*;
import static org.dcsa.core.events.model.enums.PortCallPhaseTypeCode.*;

@RequiredArgsConstructor
public enum PortCallServiceTypeCode {
    PILO(null, EnumSet.of(REQ, PLN, ACT), EnumSet.of(INBD, OUTB), "Pilotage"),
    /* Timestamp name not confirmed (not mentioned in JIT 1.1) */
    MOOR(null, Set.of(), Set.of(), "Mooring"),
    CRGO(BRTH, EnumSet.allOf(EventClassifierCode.class), EnumSet.of(INBD, ALGS), "Cargo Ops"),
    TOWG(null, EnumSet.of(REQ, PLN, ACT), EnumSet.of(INBD, OUTB), "Towage"),
    BUNK(BRTH, EnumSet.allOf(EventClassifierCode.class), EnumSet.of(INBD, ALGS), "Bunkering"),
    /* Timestamp name not confirmed (not mentioned in JIT 1.1) */
    WSDP(null, Set.of(), Set.of(), "Waste disposal"),
    /* NB: Does not follow the "XTY Name" pattern */
    FAST(null, EnumSet.of(ACT), EnumSet.of(ALGS), "AT All Fast"),
    /* NB: Does not follow the "XTY Name" pattern */
    GWAY(null, EnumSet.of(ACT), EnumSet.of(ALGS), "Gangway Down and Safe"),
    /* NB: Does not follow the "XTY Name" pattern - this has multiple names depending on context */
    SAFE(null, EnumSet.of(ACT), EnumSet.of(ALGS), null),
    LASH(BRTH, EnumSet.of(ACT), EnumSet.of(ALGS), "Lashing"),
    ;

    @Getter
    private final FacilityTypeCode expectedFacilityTypeCode;
    @Getter
    private final Set<EventClassifierCode> validEventClassifiers;
    @Getter
    private final Set<PortCallPhaseTypeCode> validPhases;
    @Getter
    private final String timestampTypeBaseName;

    public boolean isValidEventClassifierCode(EventClassifierCode eventClassifierCode) {
        return this.validEventClassifiers.contains(eventClassifierCode);
    }

    public boolean isValidPhase(PortCallPhaseTypeCode portCallPhaseTypeCode) {
        if (portCallPhaseTypeCode == null) {
            // Backwards compat: Accept null in place of a single valid phase
            return validPhases.isEmpty() || validPhases.size() == 1;
        }
        return validPhases.contains(portCallPhaseTypeCode);
    }

}
