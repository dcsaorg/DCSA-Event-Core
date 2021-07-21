package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.enums.CarrierCodeListProvider;
import org.dcsa.core.model.AuditBase;
import org.dcsa.core.model.ForeignKey;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.sql.Join;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.UUID;

@Table("vessel")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Vessel extends AuditBase {

    @Column("vessel_imo_number")
    @Size(min = 7, max = 7)
    @Pattern(regexp = "[0-9]{7}")
    @Id
    private String vesselIMONumber;

    @Size(max = 35)
    @Column("vessel_name")
    private String vesselName;

    @Size(max = 2)
    @Column("vessel_flag")
    private String vesselFlag;

    @Size(max = 10)
    @Column("vessel_call_sign_number")
    private String vesselCallSignNumber;

    @Column("vessel_operator_carrier_id")
    @JsonIgnore
    private UUID vesselOperatorCarrierID;

    @ForeignKey(fromFieldName = "vesselOperatorCarrierID", foreignFieldName = "id", joinType = Join.JoinType.LEFT_OUTER_JOIN)
    @JsonIgnore
    @Transient
    private Carrier carrier;

    public void setCarrier(Carrier carrier) {
        if (carrier != null) {
            if (carrier.getSmdgCode() != null) {
                vesselOperatorCarrierCode = carrier.getSmdgCode();
                vesselOperatorCarrierCodeListProvider = CarrierCodeListProvider.SMDG;
            } else if (carrier.getNmftaCode() != null) {
                vesselOperatorCarrierCode = carrier.getNmftaCode();
                vesselOperatorCarrierCodeListProvider = CarrierCodeListProvider.NMFTA;
            }
        }
        this.carrier = carrier;
    }

    @Transient
    private String vesselOperatorCarrierCode;

    @Transient
    private CarrierCodeListProvider vesselOperatorCarrierCodeListProvider;
}
