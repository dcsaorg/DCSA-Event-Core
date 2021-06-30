package org.dcsa.core.events.model.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.model.AuditBase;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table("transport_call")
public class AbstractTransportCall extends AuditBase {

    @Id
    @Column("id")
    protected String transportCallID;

    @Column("transport_call_sequence_number")
    private Integer transportCallSequenceNumber;

    @Size(max = 4)
    @Column("facility_type_code")
    private String facilityTypeCode;

    @Size(max = 50)
    @Column("other_facility")
    private String otherFacility;


    @JsonIgnore
    @Column("location_id")
    private UUID locationID;

    @JsonIgnore
    @Column("facility_id")
    private UUID facilityID;

}
