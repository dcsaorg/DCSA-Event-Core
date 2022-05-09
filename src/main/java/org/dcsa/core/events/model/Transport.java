package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@Table("transport")
public class Transport {

    @Id
    @Column("id")
    private UUID transportID;

    @Column("transport_reference")
    @Size(max = 50)
    private String transportReference;

    @Column("transport_name")
    @Size(max = 100)
    private String transportName;

    @JsonIgnore
    @Column("load_transport_call_id")
    private UUID loadTransportCallID;

    @JsonIgnore
    @Column("discharge_transport_call_id")
    private UUID dischargeTransportCallID;

}
