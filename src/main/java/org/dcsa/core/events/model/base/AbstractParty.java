package org.dcsa.core.events.model.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.dcsa.core.model.GetId;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import javax.validation.constraints.Size;

@Data
public class AbstractParty implements GetId<String> {

    @Id
    @JsonIgnore
    private String id;

    @Column("party_name")
    @Size(max = 100)
    private String partyName;

    @Column("tax_reference_1")
    @Size(max = 20)
    private String taxReference1;

    @Column("tax_reference_2")
    @Size(max = 20)
    private String taxReference2;

    @Column("public_key")
    @Size(max = 500)
    private String publicKey;
}
