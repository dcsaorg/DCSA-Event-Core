package org.dcsa.core.events.maper;

import org.dcsa.core.events.model.UtilizedTransportEquipment;
import org.dcsa.core.events.model.enums.WeightUnit;
import org.dcsa.core.events.model.mapper.UtilizedTransportEquipmentMapper;
import org.dcsa.core.events.model.transferobjects.UtilizedTransportEquipmentTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test for UtilizedTransportEquipmentMapper")
public class UtilizedTransportEquipmentMapperTest {

  @Spy
  UtilizedTransportEquipmentMapper utilizedTransportEquipmentMapper =
      Mappers.getMapper(UtilizedTransportEquipmentMapper.class);

  UtilizedTransportEquipmentTO utilizedTransportEquipmentTO;

  @BeforeEach
  void init() {
    utilizedTransportEquipmentTO = new UtilizedTransportEquipmentTO();
    utilizedTransportEquipmentTO.setCarrierBookingReference("carrierBookingReference1");
    utilizedTransportEquipmentTO.setCargoGrossWeight(120.0F);
    utilizedTransportEquipmentTO.setCargoGrossWeightUnit(WeightUnit.KGM);
  }

  @Test
  @DisplayName(
      "Test UtilizedTransportEquipmentTO.Id is not mapped in utilizedTransportEquipmentMapper.dtoToUtilizedTransportEquipment")
  void testUtilizedTransportEquipmentMapperExcludesIdFromTO() {
    UUID shipmentID = UUID.randomUUID();
    UUID utilizedTransportEquipmentTOId = UUID.randomUUID();
    utilizedTransportEquipmentTO.setId(utilizedTransportEquipmentTOId);
    UtilizedTransportEquipment utilizedTransportEquipment =
        utilizedTransportEquipmentMapper.dtoToUtilizedTransportEquipment(
            utilizedTransportEquipmentTO, shipmentID);
    assertNull(utilizedTransportEquipment.getId());
  }
}
