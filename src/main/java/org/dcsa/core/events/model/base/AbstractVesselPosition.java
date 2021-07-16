package org.dcsa.core.events.model.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.dcsa.core.model.GetId;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import javax.validation.constraints.Size;

// TODO: Does not necessarily reflect the IM
@Data
public class AbstractVesselPosition implements GetId<String> {

    @Id
    @JsonProperty("vesselPositionID")
    private String id;

    @Column("latitude")
    @Size(max = 10)
    private String latitude;

    @Column("longitude")
    @Size(max = 11)
    private String longitude;
}
