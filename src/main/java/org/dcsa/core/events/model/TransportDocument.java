package org.dcsa.core.events.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("transport_document")
public class TransportDocument extends AbstractTransportDocument {

    @Column("place_of_issue_id")
    private UUID placeOfIssue;

    @Column("issuing_party_id")
    private UUID issuingParty;
}
