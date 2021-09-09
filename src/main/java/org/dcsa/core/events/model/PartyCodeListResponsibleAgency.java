package org.dcsa.core.events.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("party_code_list_responsible_agency")
public class PartyCodeListResponsibleAgency {

  @Id private UUID id;

  @Column("code_list_responsible_agency_code")
  private String codeListResponsibleAgencyCode;

  @Column("party_id")
  private String partyID;

  @Column("party_code")
  private String partyCode;
}
