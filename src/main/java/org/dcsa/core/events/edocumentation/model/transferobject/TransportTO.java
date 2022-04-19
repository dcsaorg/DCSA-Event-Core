package org.dcsa.core.events.edocumentation.model.transferobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.dcsa.core.events.model.enums.DCSATransportType;
import org.dcsa.core.events.model.enums.TransportPlanStageCode;
import org.dcsa.skernel.validator.ValidVesselIMONumber;
import org.dcsa.skernel.model.transferobjects.LocationTO;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Data
public
class TransportTO {

  @Size(max = 50)
  private String transportReference;

  @Size(max = 100)
  private String transportName;

  @NotNull(message = "TransportPlanStage is required.")
  private TransportPlanStageCode transportPlanStage;

  private int transportPlanStageSequenceNumber;

  @NotNull(message = "LoadLocation is required.")
  private LocationTO loadLocation;

  @NotNull(message = "DischargeLocation is required.")
  private LocationTO dischargeLocation;

  @NotNull(message = "PlannedDepartureDate is required.")
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private OffsetDateTime plannedDepartureDate;

  @NotNull(message = "PlannedArrivalDate is required.")
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private OffsetDateTime plannedArrivalDate;

  private DCSATransportType modeOfTransport;

  @Size(max = 35, message = "VesselName has a max size of 35.")
  private String vesselName;

  @ValidVesselIMONumber(allowNull = true, message = "VesselIMONumber is invalid.")
  private String vesselIMONumber;

  @Size(max = 50, message = "ImportVoyageNumber has a max size of 50.")
  private String importVoyageNumber;

  @Size(max = 50, message = "ExportVoyageNumber has a max size of 50.")
  private String exportVoyageNumber;

  private Boolean isUnderShippersResponsibility;
}
