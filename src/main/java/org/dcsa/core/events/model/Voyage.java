package org.dcsa.core.events.model;

import lombok.Data;
import org.dcsa.core.model.ForeignKey;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("voyage")
public class Voyage {

    @Id
    private UUID id;

    @Column("carrier_voyage_number")
    private String carrierVoyageNumber;

    @Column("service_id")
    private UUID serviceID;

    @Transient
    @ForeignKey(fromFieldName = "serviceID", foreignFieldName = "id")
    private Service service;
}
