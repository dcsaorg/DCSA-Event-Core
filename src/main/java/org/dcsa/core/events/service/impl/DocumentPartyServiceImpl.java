package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.*;
import org.dcsa.core.events.model.transferobjects.DocumentPartyTO;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.core.events.repository.*;
import org.dcsa.core.events.service.AddressService;
import org.dcsa.core.events.service.DocumentPartyService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DocumentPartyServiceImpl implements DocumentPartyService {

	private final DocumentPartyRepository documentPartyRepository;
	private final DisplayedAddressRepository displayedAddressRepository;
	private final PartyRepository partyRepository;
	private final PartyIdentifyingCodeRepository partyIdentifyingCodeRepository;
	private final PartyContactDetailsRepository partyContactDetailsRepository;

	private final AddressService addressService;

	@Override
	public Mono<Optional<List<DocumentPartyTO>>> createDocumentPartiesByBookingID(UUID bookingID, List<DocumentPartyTO> documentParties) {
		return createDocumentParties(Map.of("bookingID", bookingID.toString()), documentParties);
	}

	@Override
	public Mono<Optional<List<DocumentPartyTO>>> createDocumentPartiesByShippingInstructionID(String shippingInstructionID, List<DocumentPartyTO> documentParties) {
		return createDocumentParties(Map.of("shippingInstructionID", shippingInstructionID), documentParties);
	}

	private Mono<Optional<List<DocumentPartyTO>>> createDocumentParties(Map<String, String> foreignKey, List<DocumentPartyTO> documentParties) {
		if (Objects.isNull(documentParties) || documentParties.isEmpty()) {
			return Mono.just(Optional.of(Collections.emptyList()));
		}

		return Flux.fromStream(documentParties.stream())
			.flatMap(
				dp ->
					// party is mandatory, cannot be null in document party as per API specs
					createPartyByTO(dp.getParty())
						.flatMap(
							t -> {
								DocumentParty documentParty = getDocumentParty(foreignKey, t.getT1(), dp);
								return documentPartyRepository
									.save(documentParty)
									.map(
										savedDp -> {
											DocumentPartyTO documentPartyTO = new DocumentPartyTO();
											documentPartyTO.setParty(t.getT2());
											documentPartyTO.setDisplayedAddress(dp.getDisplayedAddress());
											documentPartyTO.setPartyFunction(savedDp.getPartyFunction());
											documentPartyTO.setIsToBeNotified(savedDp.getIsToBeNotified());
											return Tuples.of(savedDp.getId(), documentPartyTO);
										});
							}))
			.flatMap(
				t -> {
					Stream<DisplayedAddress> displayedAddressStream =
						t.getT2().getDisplayedAddress().stream()
							.map(
								da -> {
									DisplayedAddress displayedAddress = new DisplayedAddress();
									displayedAddress.setDocumentPartyID(t.getT1());
									displayedAddress.setAddressLine(da);
									displayedAddress.setAddressLineNumber(
										t.getT2().getDisplayedAddress().indexOf(da));
									return displayedAddress;
								});

					return displayedAddressRepository
						.saveAll(Flux.fromStream(displayedAddressStream))
						.map(DisplayedAddress::getAddressLine)
						.collectList()
						.flatMap(
							s -> {
								t.getT2().setDisplayedAddress(s);
								return Mono.just(t.getT2());
							});
				})
			.collectList()
			.map(Optional::of);
	}

	private Mono<Tuple2<String, PartyTO>> createPartyByTO(final PartyTO partyTO) {

		Mono<Tuple2<String, PartyTO>> partyMap;

		if (Objects.isNull(partyTO.getAddress())) {

			partyMap =
				partyRepository
					.save(partyTO.toParty())
					.map(p -> Tuples.of(p.getId(), p.toPartyTO(partyTO.getNmftaCode(), partyTO.getAddress(), partyTO.getIdentifyingCodes())));

		} else {
			// if there is an address connected to the party, we need to create it first.
			partyMap =
				addressService
					.ensureResolvable(partyTO.getAddress())
					.flatMap(
						a -> {
							Party party = partyTO.toParty();
							return partyRepository
								.save(party)
								.map(
									p -> {
										PartyTO pTO = p.toPartyTO(partyTO.getNmftaCode(), partyTO.getAddress(), partyTO.getIdentifyingCodes());
										return Tuples.of(p.getId(), pTO);
									});
						});
		}

		return partyMap
			.flatMap(
				t -> {
					Stream<PartyContactDetails> partyContactDetailsStream =
						partyTO.getPartyContactDetails().stream()
							.map(
								pcdTO -> {
									PartyContactDetails pcd =
										pcdTO.toPartyContactDetails(t.getT1());
									return pcd;
								});

					return partyContactDetailsRepository
						.saveAll(Flux.fromStream(partyContactDetailsStream))
						.map(partyContactDetails -> partyContactDetails.toPartyTO())
						.collectList()
						.flatMap(
							pcds -> {
								t.getT2().setPartyContactDetails(pcds);
								return Mono.just(t);
							});
				})
			.flatMap(
				t -> {
					Stream<PartyIdentifyingCode> partyIdentifyingCodeStream =
						partyTO.getIdentifyingCodes().stream()
							.map(
								idc -> {
									PartyIdentifyingCode partyIdentifyingCode = new PartyIdentifyingCode();
									partyIdentifyingCode.setPartyID(t.getT1());
									partyIdentifyingCode.setDcsaResponsibleAgencyCode(
										idc.getDcsaResponsibleAgencyCode());
									partyIdentifyingCode.setCodeListName(idc.getCodeListName());
									partyIdentifyingCode.setPartyCode(idc.getPartyCode());
									return partyIdentifyingCode;
								});
					return partyIdentifyingCodeRepository
						.saveAll(
							Flux.fromStream(
								partyIdentifyingCodeStream)) // save identifying codes related to party
						// obj
						.map(
							savedIdcs ->
								PartyTO.IdentifyingCode.builder()
									.partyCode(savedIdcs.getPartyCode())
									.codeListName(savedIdcs.getCodeListName())
									.dcsaResponsibleAgencyCode(savedIdcs.getDcsaResponsibleAgencyCode())
									.build())
						.collectList()
						.flatMap(
							identifyingCodes -> {
								PartyTO pTO = t.getT2();
								pTO.setIdentifyingCodes(identifyingCodes);
								return Mono.just(Tuples.of(t.getT1(), pTO));
							});
				});
	}

	private DocumentParty getDocumentParty(Map<String, String> foreignKey, String partyID, DocumentPartyTO documentPartyTO) {
		DocumentParty documentParty = new DocumentParty();
		documentParty.setPartyID(partyID);
		documentParty.setPartyFunction(documentPartyTO.getPartyFunction());
		documentParty.setIsToBeNotified(documentPartyTO.getIsToBeNotified());

		foreignKey.forEach((foreignKeyType, foreignKeyValue) -> {
			if (foreignKeyType.equals("bookingID")) {
				documentParty.setBookingID(UUID.fromString(foreignKeyValue));
			}
			if (foreignKeyType.equals("shippingInstructionID")) {
				documentParty.setShippingInstructionID(foreignKeyValue);
			}
			if (foreignKeyType.equals("shipmentID")) {
				documentParty.setShipmentID(UUID.fromString(foreignKeyValue));
			}
		});
		return documentParty;
	}

}
