package org.dcsa.core.events.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dcsa.core.events.model.enums.WeightUnit;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface UtilizedTransportEquipmentCustomRepository {

  Flux<UtilizedTransportEquipmentDetails> findUtilizedTransportEquipmentDetailsByShipmentID(UUID shipmentID);

	@Getter
	@AllArgsConstructor
	class UtilizedTransportEquipmentDetails {
    private String carrierBookingReference;
		private String equipmentReference;
		private Float cargoGrossWeight;
		private WeightUnit cargoGrossWeightUnit;
		private String isoEquipmentCode;
		private Float tareWeight;
		private String weightUnit;
		private Boolean isShipperOwned;
		private UUID utilizedTransportEquipmentID;
	}
}
