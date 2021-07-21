package org.dcsa.core.events.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Size;
import java.util.UUID;

@Table("carrier")
@Data
@NoArgsConstructor
public class Carrier {

    @Id
    @Column("id")
    private UUID id;

    @Size(max = 100)
    @Column("carrier_name")
    private String carrierName;

    @Size(max = 3)
    @Column("smdg_code")
    private String smdgCode;

    @Size(max = 4)
    @Column("nmfta_code")
    private String nmftaCode;
}
