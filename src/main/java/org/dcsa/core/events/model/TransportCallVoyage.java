package org.dcsa.core.events.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.util.UUID;

@Data
@NoArgsConstructor
public class TransportCallVoyage {

    @Id
    /* phoney */
    protected UUID id;

    @Column("voyage_id")
    protected UUID voyageID;

    @Column("transport_call_id")
    private String transportCallID;

}
