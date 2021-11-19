package org.dcsa.core.events.repository.impl;

import org.dcsa.core.events.model.enums.DocumentStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;

import java.util.UUID;

import static org.springframework.data.relational.core.query.Criteria.where;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for CustomBookingRepository Implementation.")
public class BookingCustomRepositoryImplTest {

	@Mock R2dbcEntityTemplate r2dbcEntityTemplate;

	@InjectMocks BookingCustomRepositoryImpl bookingCustomRepository;

	@Test
	@DisplayName("Get booking summaries without filters should return a query with an empty criteria")
	void bookingSummaryRequestWithoutFiltersShouldReturnEmptyCriteria() {
		Criteria criteria = Criteria.from(bookingCustomRepository.getCriteriaHasCarrierBookingRequestReference(null), bookingCustomRepository.getCriteriaHasDocumentStatus(null));
		Criteria expectedCriteria = Criteria.from(Criteria.empty(), Criteria.empty());
		Assertions.assertEquals(expectedCriteria.getValue(), criteria.getValue());
	}

	@Test
	@DisplayName("Get booking summaries with carrierRequestBookingReference filter should return a query with an one criteria present")
	void bookingSummaryRequestWithCarrierRequestBookingReferenceFilterShouldReturnCarrierRequestBookingReferenceCriteria() {
		String carrierBookingRequestReference = UUID.randomUUID().toString();
		Criteria criteria = Criteria.from(bookingCustomRepository.getCriteriaHasCarrierBookingRequestReference(carrierBookingRequestReference), bookingCustomRepository.getCriteriaHasDocumentStatus(null));
		Criteria expectedCriteria = Criteria.from(where("carrier_booking_request_reference").is(carrierBookingRequestReference), Criteria.empty());
		Assertions.assertEquals(expectedCriteria.getGroup().get(0).toString(), criteria.getGroup().get(0).toString());
		Assertions.assertEquals(expectedCriteria.getGroup().get(1).toString(), criteria.getGroup().get(1).toString());
	}

	@Test
	@DisplayName("Get booking summaries with documentStatus Filter should return a query with an one criteria present")
	void bookingSummaryRequestWithDocumentStatusFilterShouldReturnCarrierRequestBookingReferenceCriteria() {
		DocumentStatus documentStatus = DocumentStatus.ISSU;
		Criteria criteria = Criteria.from(bookingCustomRepository.getCriteriaHasCarrierBookingRequestReference(null), bookingCustomRepository.getCriteriaHasDocumentStatus(documentStatus));
		Criteria expectedCriteria = Criteria.from(Criteria.empty(), where("document_status").is(documentStatus));
		Assertions.assertEquals(expectedCriteria.getGroup().get(0).toString(), criteria.getGroup().get(0).toString());
		Assertions.assertEquals(expectedCriteria.getGroup().get(1).toString(), criteria.getGroup().get(1).toString());
	}

	@Test
	@DisplayName("Get booking summaries with carrierRequestBookingReference and documentStatus filters should return a query with two criterias")
	void bookingSummaryRequestWithCarrierRequestBookingReferenceAndDocumentStatusFilterShouldReturnCarrierRequestBookingReferenceCriteria() {
		String carrierBookingRequestReference = UUID.randomUUID().toString();
		DocumentStatus documentStatus = DocumentStatus.REJE;
		Criteria criteria = Criteria.from(bookingCustomRepository.getCriteriaHasCarrierBookingRequestReference(carrierBookingRequestReference), bookingCustomRepository.getCriteriaHasDocumentStatus(documentStatus));
		Criteria expectedCriteria = Criteria.from(where("carrier_booking_request_reference").is(carrierBookingRequestReference), where("document_status").is(documentStatus));
		Assertions.assertEquals(expectedCriteria.getGroup().get(0).toString(), criteria.getGroup().get(0).toString());
		Assertions.assertEquals(expectedCriteria.getGroup().get(1).toString(), criteria.getGroup().get(1).toString());
	}
}
