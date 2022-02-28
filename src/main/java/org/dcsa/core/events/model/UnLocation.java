package org.dcsa.core.events.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Size;

@Data
@Table("un_location")
public class UnLocation {
  @Id
  @Size(max = 5)
  @Column("un_location_code")
  private String unLocationCode;

  @Size(max = 100)
  @Column("un_location_name")
  private String unLocationName;

  @Size(max = 3)
  @Column("location_code")
  private String locationCode;

  @Size(max = 2)
  @Column("country_code")
  private String countryCode;
}
