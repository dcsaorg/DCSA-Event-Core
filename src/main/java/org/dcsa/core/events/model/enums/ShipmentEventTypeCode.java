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
    APPR("Approved"),
    ISSU("Issued"),
    SURR("Surrenered"),
    SUBM("Submitted"),
    VOID("Void"),

    CONF("Confirmed"),
    PENC("Pending Confirmation"),
    CANC("Cancelled"),
    CMPL("Completed"),
    RELS("Released"),
    REQS("Requested"),
    HOLD("On hold");


    /**
     * The values allowed when used as a DocumentStatus
     */
    public static final String DOCUMENT_STATUSES = "RECE,PENU,REJE,CONF,PENC,CANC";
    private final String value;
}
