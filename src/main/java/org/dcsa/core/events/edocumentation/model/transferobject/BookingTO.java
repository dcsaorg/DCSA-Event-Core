package org.dcsa.core.events.edocumentation.model.transferobject;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.transferobjects.DocumentPartyTO;
import org.dcsa.core.events.model.transferobjects.ReferenceTO;
import org.dcsa.skernel.model.transferobjects.LocationTO;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookingTO extends AbstractBookingTO {

  private LocationTO invoicePayableAt;

  private LocationTO placeOfIssue;

  @Valid
  @NotEmpty(message = "The attribute commodities is required.")
  private List<CommodityTO> commodities;

  @Valid private List<ValueAddedServiceRequestTO> valueAddedServiceRequests;

  @Valid private List<ReferenceTO> references;

  @Valid private List<RequestedEquipmentTO> requestedEquipments;

  @Valid private List<DocumentPartyTO> documentParties;

  @Valid private List<ShipmentLocationTO> shipmentLocations;
}
