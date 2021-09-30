package org.dcsa.core.events.model.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dcsa.core.model.GetId;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@NoArgsConstructor
@Table("location")
public class AbstractLocation {

    @Id
    @JsonIgnore
    private String id;

    @Column("location_name")
    @Size(max = 100)
    private String locationName;

    @Size(max = 10)
    private String latitude;

    @Size(max = 11)
    private String longitude;

    @JsonProperty("UNLocationCode")
    @Column("un_location_code")
    @Size(max = 5)
    // Mismatch between JSON name and Java field name due to how the Core mapper works.
    private String unLocationCode;

    @JsonIgnore
    @Column("address_id")
    private UUID addressID;

    @JsonIgnore
    @Column("facility_id")
    private UUID facilityID;
}
