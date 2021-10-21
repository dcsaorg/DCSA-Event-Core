package org.dcsa.core.events.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("negotiation_cycle")
@Data
public class NegotiationCycle {
    @Id
    @Column("cycle_key")
    private String cycleKey;

    @Column("name")
    private String cycleName;
}
