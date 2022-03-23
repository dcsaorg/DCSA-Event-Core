package org.dcsa.core.events.model.mapper;

import org.dcsa.core.events.edocumentation.model.transferobject.ValueAddedServiceRequestTO;
import org.dcsa.core.events.model.ValueAddedServiceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import javax.validation.constraints.Size;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ValueAddedServiceRequestMapper {

  @Mapping(source = "valueAddedServiceRequestTO.valueAddedServiceCode", target = "valueAddedServiceCode")
  @Mapping(source = "bookingID", target = "bookingID")
  ValueAddedServiceRequest dtoToValueAddedServiceRequestWithBookingID(
      ValueAddedServiceRequestTO valueAddedServiceRequestTO, UUID bookingID);

  @Mapping(source = "valueAddedServiceRequestTO.valueAddedServiceCode", target = "valueAddedServiceCode")
  @Mapping(source = "shippingInstructionID", target = "shippingInstructionID")
  ValueAddedServiceRequest dtoToValueAddedServiceRequestWithShippingInstructionID(
      ValueAddedServiceRequestTO valueAddedServiceRequestTO,
      @Size(max = 100) String shippingInstructionID);

  @Mapping(source = "valueAddedServiceCode", target = "valueAddedServiceCode")
  ValueAddedServiceRequestTO ValueAddedServiceRequestToDTO(
      ValueAddedServiceRequest valueAddedServiceRequest);
}
