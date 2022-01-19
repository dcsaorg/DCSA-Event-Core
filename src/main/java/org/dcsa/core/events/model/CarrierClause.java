package org.dcsa.core.events.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("carrier_clauses")
@Data
public class CarrierClause {

  @Id
  private UUID id;

  @Column("clause_content")
  private String clauseContent;
}
