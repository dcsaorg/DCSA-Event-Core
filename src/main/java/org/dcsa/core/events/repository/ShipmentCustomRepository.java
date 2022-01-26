package org.dcsa.core.events.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dcsa.core.events.model.enums.ShipmentEventTypeCode;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;

public interface ShipmentCustomRepository {

	Flux<ShipmentSummary> findShipmentsAndBookingsByDocumentStatus(ShipmentEventTypeCode documentStatus, Pageable pageable);

	@Getter
	@AllArgsConstructor
	class ShipmentSummary {

		private String carrierBookingReference;

		private String termsAndConditions;

		private OffsetDateTime confirmationDateTime;

		private OffsetDateTime updatedDateTime;

		private String carrierBookingRequestReference;

		private ShipmentEventTypeCode documentStatus;

	}
}
