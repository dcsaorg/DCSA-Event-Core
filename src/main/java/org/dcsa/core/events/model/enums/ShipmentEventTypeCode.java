package org.dcsa.core.events.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ShipmentEventTypeCode {
    RECE("Received"),
    DRFT("Draft"),
    PENA("Pending Approval"),

    PENU("Pending Update"),
    REJE("Rejected"),
    APPR(null),
    ISSU(null),
    SURR(null),
    SUBM(null),
    VOID(null),
    CONF("Confirmed"),
    PENC("Pending Confirmation"),
    CANC("Cancelled"),
    CMPL(null),
    RELS(null),
    REQS(null),
    HOLD(null);

    /**
     * The values allowed when used as a DocumentStatus
     */
    public static final String DOCUMENT_STATUSES = "RECE,PENU,REJE,CONF,PENC,CANC";
    private final String value;
}
