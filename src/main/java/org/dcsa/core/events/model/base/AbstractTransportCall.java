package org.dcsa.core.events.model.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.model.AuditBase;
import org.dcsa.core.validator.EnumSubset;
import org.dcsa.skernel.model.enums.FacilityTypeCode;
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
    protected UUID transportCallID;

    @Column("transport_call_reference")
    protected String transportCallReference;

    @Column("transport_call_sequence_number")
    private Integer transportCallSequenceNumber;

    @Size(max = 4)
    @Column("facility_type_code")
    @EnumSubset(anyOf = {"BOCR", "CLOC", "COFS", "COYA", "OFFD", "DEPO", "INTE", "POTE"})
    private FacilityTypeCode facilityTypeCode;

    @Size(max = 50)
    @Column("other_facility")
    private String otherFacility;


    @JsonIgnore
    @Column("location_id")
    private String locationID;

    @JsonIgnore
    @Column("facility_id")
    private UUID facilityID;

    @JsonIgnore
    @Column("mode_of_transport_code")
    @Size(max = 3)
    private String modeOfTransportID;

    @JsonIgnore
    @Column("vessel_id")
    private UUID vesselID;

    @JsonIgnore
    @Column("import_voyage_id")
    private UUID importVoyageID;

    @JsonIgnore
    @Column("export_voyage_id")
    private UUID exportVoyageID;
}
