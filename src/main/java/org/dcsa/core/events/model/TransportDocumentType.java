package org.dcsa.core.events.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("transport_document_type")
public class TransportDocumentType {
  @Id
  @Column("transport_document_type_code")
  private String code;

  @Column("transport_document_type_name")
  private String name;

  @Column("transport_document_type_description")
  private String description;
}
