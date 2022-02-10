package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.model.AuditBase;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@NoArgsConstructor
@Data
@Table("seal")
@EqualsAndHashCode(callSuper = true)
public class Seal extends AuditBase {

    @Id
    @JsonIgnore
    private UUID id;

    @JsonIgnore
    @Column("shipment_equipment_id")
    @NotNull
    private UUID shipmentEquipmentID;

    @Column("seal_number")
    @Size(max = 15)
    @NotNull
    private String sealNumber;

    @Column("seal_source_code")
    @Size(max = 5)
    @NotNull
    private String sealSource;

    @Column("seal_type_code")
    @Size(max = 5)
    @NotNull
    private String sealType;

//    public SealTO toSealTO() {
//        SealTO sealTO = new SealTO();
//        sealTO.setSealNumber(this.getSealNumber());
//        sealTO.setSealSource(SealSourceCode.valueOf(this.getSealSource()));
//        sealTO.setSealType(SealTypeCode.valueOf(this.getSealType()));
//        return sealTO;
//    }
}
