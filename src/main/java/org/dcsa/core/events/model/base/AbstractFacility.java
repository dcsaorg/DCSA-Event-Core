package org.dcsa.core.events.model.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@Table("facility")
public class AbstractFacility {

    @Id
    @Column("id")
    private UUID facilityID;

    @Size(max = 100)
    @Column("facility_name")
    private String facilityName;

    @Size(max = 5)
    @Column("un_location_code")
    @JsonProperty("UNLocationCode")
    // Mismatch between JSON name and Java field name due to how the Core mapper works.
    private String unLocationCode;

    @Size(max = 4)
    @Column("facility_bic_code")
    private String facilityBICCode;

    @Size(max = 4)
    @Column("facility_smdg_code")
    private String facilitySMGDCode;

    @JsonIgnore
    @Column("location_id")
    private String locationID;
}
