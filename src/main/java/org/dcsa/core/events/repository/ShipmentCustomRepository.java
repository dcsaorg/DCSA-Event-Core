package org.dcsa.core.events.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dcsa.core.events.model.enums.DocumentStatus;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;

public interface ShipmentCustomRepository {

	Flux<ShipmentSummary> findShipmentsAndBookingsByDocumentStatus(DocumentStatus documentStatus, Pageable pageable);

	@Getter
	@AllArgsConstructor
	class ShipmentSummary {

		private String carrierBookingReference;

		private String termsAndConditions;

		private OffsetDateTime confirmationDateTime;

		private String carrierBookingRequestReference;

		private DocumentStatus documentStatus;

	}
}
