package org.dcsa.core.events.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@Table
public class DisplayedAddress {

  @Id private UUID id;

  @Column("document_party_id")
  private UUID documentPartyID;

  @Size(max = 250)
  @Column("address_line_text")
  private String addressLine;

  @NotNull
  @Column("address_line_number")
  private Integer addressLineNumber;
}
