package org.dcsa.core.events.model;

import lombok.Data;
import org.dcsa.core.events.model.enums.DCSAResponsibleAgencyCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("party_identifying_code")
public class PartyIdentifyingCode {

  @Id
  private UUID id;

  @Column("dcsa_responsible_agency_code")
  private DCSAResponsibleAgencyCode DCSAResponsibleAgencyCode;

  @Column("party_id")
  private String partyID;

  @Column("party_code")
  private String partyCode;

  @Column("code_list_name")
  private String codeListName;
}
