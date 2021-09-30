package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Address;
import org.dcsa.core.events.model.Facility;
import org.dcsa.core.events.model.Location;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.events.repository.LocationRepository;
import org.dcsa.core.events.service.AddressService;
import org.dcsa.core.events.service.FacilityService;
import org.dcsa.core.events.service.LocationService;
import org.dcsa.core.events.util.Util;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class LocationServiceImpl extends ExtendedBaseServiceImpl<LocationRepository, Location, String> implements LocationService {
    private final LocationRepository locationRepository;
    private final FacilityService facilityService;
    private final AddressService addressService;

    @Override
    public LocationRepository getRepository() {
        return locationRepository;
    }

    public Mono<LocationTO> findPaymentLocationByShippingInstructionID(String shippingInstructionID) {
        return locationRepository.findPaymentLocationByShippingInstructionID(shippingInstructionID)
                .flatMap(this::getLocationTO);
    }

    @Override
    public Mono<LocationTO> ensureResolvable(LocationTO locationTO) {
        Address address = locationTO.getAddress();
        Mono<LocationTO> locationTOMono;
        if (address != null) {
            locationTOMono = addressService.ensureResolvable(address)
                    .doOnNext(locationTO::setAddress)
                    .thenReturn(locationTO);
        } else {
            locationTOMono = Mono.just(locationTO);
        }
        if (locationTO.getFacilityCode() != null) {
            locationTOMono = locationTOMono.flatMap(loc -> facilityService.findByUNLocationCodeAndFacilityCode(
                    loc.getUnLocationCode(),
                    loc.getFacilityCodeListProvider(),
                    loc.getFacilityCode())
                ).doOnNext(locationTO::setFacility)
                .thenReturn(locationTO);
        }

        return locationTOMono
                .flatMap(locTo -> Util.createOrFindByContent(
                        locTo,
                        locationRepository::findByContent,
                        locTO -> this.create(locTO.toLocation())
                )).map(location -> location.toLocationTO(locationTO.getAddress(), locationTO.getFacility()));
    }

    @Override
    public Mono<LocationTO> findTOById(String locationID) {
        return findById(locationID)
                .flatMap(this::getLocationTO);
    }

    private Mono<LocationTO> getLocationTO(Location location) {
        // Use single-element arrays to work around Java
        Address[] addresses = new Address[1];
        Facility[] facilities = new Facility[1];
        return Mono.justOrEmpty(location.getAddressID())
                .flatMap(addressService::findById)
                .doOnNext(address -> addresses[0] = address)
                .then(Mono.justOrEmpty(location.getFacilityID()))
                .flatMap(facilityService::findById)
                .doOnNext(facility -> facilities[0] = facility)
                .thenReturn(location)
                .map(loc -> loc.toLocationTO(addresses[0], facilities[0]));
    }
}
