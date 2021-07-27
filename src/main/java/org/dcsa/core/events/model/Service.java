package org.dcsa.core.events.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("service")
public class Service {
  @Id private UUID id;

  @Column("carrier_id")
  private UUID carrierID;

  @Column("carrier_service_code")
  private String carrierServiceCode;

  @Column("carrier_service_name")
  private String carrierServiceName;

  @Column("tradelane_id")
  private String tradelaneID;
}
