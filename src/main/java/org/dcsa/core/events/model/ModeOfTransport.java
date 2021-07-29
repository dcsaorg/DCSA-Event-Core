package org.dcsa.core.events.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("mode_of_transport")
public class ModeOfTransport {
    @Id
    @Column("mode_of_transport_code")
    private String id;

    @Column("mode_of_transport_name")
    private String name;

    @Column("mode_of_transport_description")
    private String description;

    @Column(value = "dcsa_transport_type")
    private String dcsaTransportType;
}
