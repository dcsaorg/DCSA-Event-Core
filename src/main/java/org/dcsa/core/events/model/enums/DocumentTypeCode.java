package org.dcsa.core.events.model.enums;

public enum DocumentTypeCode {
    BKG,
    @Deprecated
    BOK,
    SHI,
    VGM,
    SRM,
    TRD,
    ARN,
    CBR,
    CAS,
    CUS,
    DGD,
    OOG;

    public final static String EBL_DOCUMENT_TYPE_CODES = "SHI,TRD";
    public final static String BKG_DOCUMENT_TYPE_CODES = "BKG,CBR";

}
