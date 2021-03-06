package org.dcsa.core.events.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("transport_document")
public class TransportDocument extends AbstractTransportDocument {

    @Column("place_of_issue")
    private String placeOfIssue;

    @Column("issuingParty")
    private String issuingParty;
}
