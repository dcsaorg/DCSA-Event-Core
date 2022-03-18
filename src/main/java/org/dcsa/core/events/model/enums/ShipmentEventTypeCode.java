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
    SURR("Surrendered"),
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
     * The values allowed when used as a DocumentStatus in Booking.
     */
    public static final String BOOKING_DOCUMENT_STATUSES = "RECE,PENU,REJE,CONF,PENC,CANC,CMPL";

    /**
     * The values allowed when used as a DocumentStatus in EBL.
     */
    public final static String EBL_DOCUMENT_STATUSES = "RECE,PENU,DRFT,PENA,APPR,ISSU,SURR,VOID";

    private final String value;
}
