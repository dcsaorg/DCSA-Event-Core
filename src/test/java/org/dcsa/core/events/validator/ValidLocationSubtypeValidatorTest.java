package org.dcsa.core.events.validator;

import lombok.Data;
import org.dcsa.core.events.model.Address;
import org.dcsa.core.events.model.enums.FacilityCodeListProvider;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class ValidLocationSubtypeValidatorTest {

    private static final LocationTO GEO_COORDINATE_LOCATION = withGeoCoordinates(new LocationTO());
    private static final LocationTO UN_LOCATION_LOCATION = withUnLocation(new LocationTO());
    private static final LocationTO FACILITY_LOCATION = withFacility(new LocationTO());
    private static final LocationTO ADDRESS_LOCATION = withAddress(new LocationTO());
    private static final LocationTO ADDRESS_WITH_GEO_COORDINATE_LOCATION = withGeoCoordinates(withAddress(new LocationTO()));
    private static final LocationTO EMPTY_LOCATION = new LocationTO();

    private static Validator validator;

    @BeforeAll
    static void beforeClass() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testBasicValidCases() {
        Assertions.assertTrue(validate(LocationHolder.of(
                GEO_COORDINATE_LOCATION,
                UN_LOCATION_LOCATION,
                UN_LOCATION_LOCATION,  // <-- POTE can be either UN or facility
                FACILITY_LOCATION,
                ADDRESS_LOCATION
        )));

        Assertions.assertTrue(validate(LocationHolder.of(
                GEO_COORDINATE_LOCATION,
                UN_LOCATION_LOCATION,
                FACILITY_LOCATION,  // <-- POTE can be either UN or facility
                FACILITY_LOCATION,
                ADDRESS_LOCATION
        )));

        Assertions.assertTrue(validate(LocationHolder.of(
                ADDRESS_WITH_GEO_COORDINATE_LOCATION,  // Counts as both, so it is fine
                UN_LOCATION_LOCATION,
                FACILITY_LOCATION,  // <-- POTE can be either UN or facility
                FACILITY_LOCATION,
                ADDRESS_WITH_GEO_COORDINATE_LOCATION // Counts as both, so it is fine
        )));
    }

    @Test
    void testBasicInvalidCases() {
        Assertions.assertEquals(2, issueCount(LocationHolder.of(
                UN_LOCATION_LOCATION,  // UN and Geo are swapped
                GEO_COORDINATE_LOCATION,
                UN_LOCATION_LOCATION,
                FACILITY_LOCATION,
                ADDRESS_LOCATION
        )));

        Assertions.assertEquals(1, issueCount(LocationHolder.of(
                GEO_COORDINATE_LOCATION,
                UN_LOCATION_LOCATION,
                EMPTY_LOCATION,  // Does not count as a facility or a un location
                FACILITY_LOCATION,
                ADDRESS_LOCATION
        )));
    }

    @Test
    void testDoubleRestriction() {
        Assertions.assertTrue(validate(DoubleRestriction.of(ADDRESS_WITH_GEO_COORDINATE_LOCATION)));
        Assertions.assertEquals(1, issueCount(DoubleRestriction.of(ADDRESS_LOCATION)));
        Assertions.assertEquals(1, issueCount(DoubleRestriction.of(GEO_COORDINATE_LOCATION)));
    }

    @Test
    void testNull() {
        Assertions.assertTrue(validate(DoubleRestriction.of(null)));
    }


    private <T> boolean validate(T value) {
        return issueCount(value) == 0;
    }

    private <T> int issueCount(T value) {
        Set<ConstraintViolation<T>> issues = validator.validate(value);
        return issues.size();
    }

    private static LocationTO withGeoCoordinates(LocationTO locationTO) {
        locationTO.setLatitude("48.8585500");
        locationTO.setLongitude("2.294492036");
        return locationTO;
    }

    private static LocationTO withAddress(LocationTO locationTO) {
        Address address = new Address();
        address.setStreet("Kronprincessegade");
        address.setStreetNumber("54");
        address.setFloor("5. sal");
        address.setPostalCode("1306");
        address.setCity("København");
        address.setCountry("Denmark");
        locationTO.setAddress(address);
        return locationTO;
    }

    private static LocationTO withUnLocation(LocationTO locationTO) {
        locationTO.setUnLocationCode("DEHAM");
        return locationTO;
    }

    private static LocationTO withFacility(LocationTO locationTO) {
        locationTO.setUnLocationCode("DEHAM");
        locationTO.setFacilityCode("EGH");
        locationTO.setFacilityCodeListProvider(FacilityCodeListProvider.SMDG);
        return locationTO;
    }

    @Data(staticConstructor = "of")
    private static class LocationHolder {
        @ValidLocationSubtype(anyOf = ValidLocationSubtype.LocationSubtype.GEO_COORDINATE)
        private final LocationTO vesselPosition;

        @ValidLocationSubtype(anyOf = ValidLocationSubtype.LocationSubtype.UN_LOCATION)
        private final LocationTO portLocation;

        @ValidLocationSubtype(anyOf = {ValidLocationSubtype.LocationSubtype.FACILITY, ValidLocationSubtype.LocationSubtype.UN_LOCATION})
        private final LocationTO poteLocation;

        @ValidLocationSubtype(anyOf = ValidLocationSubtype.LocationSubtype.FACILITY)
        private final LocationTO facilityLocation;

        @ValidLocationSubtype(anyOf = ValidLocationSubtype.LocationSubtype.ADDRESS)
        private final LocationTO addressLocation;
    }

    @Data(staticConstructor = "of")
    private static class DoubleRestriction {

        @ValidLocationSubtype(anyOf = ValidLocationSubtype.LocationSubtype.GEO_COORDINATE)
        @ValidLocationSubtype(anyOf = ValidLocationSubtype.LocationSubtype.ADDRESS)
        private final LocationTO geoWithAddressLocation;
    }

}
