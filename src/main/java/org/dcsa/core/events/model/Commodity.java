package org.dcsa.core.events.model;

import lombok.Data;
import org.dcsa.core.events.model.enums.VolumeUnit;
import org.dcsa.core.events.model.enums.WeightUnit;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Table("commodity")
public class Commodity {

  @Id private UUID id;

  @Column("booking_id")
  private UUID bookingID;

  @Column("commodity_type")
  @Size(max = 550)
  private String commodityType;

  @Column("hs_code") // we need an enum for this
  private String hsCode;

  @Column("cargo_gross_weight")
  private Double cargoGrossWeight;

  @Column("cargo_gross_weight_unit")
  private WeightUnit cargoGrossWeightUnit;

  @Column("cargo_gross_volume")
  private Float cargoGrossVolume;

  @Column("cargo_gross_volume_unit")
  private VolumeUnit cargoGrossVolumeUnit;

  @Column("number_of_packages")
  private Integer numberOfPackages;

  @Column("export_license_issue_date")
  private LocalDate exportLicenseIssueDate;

  @Column("export_license_expiry_date")
  private LocalDate exportLicenseExpiryDate;
}
