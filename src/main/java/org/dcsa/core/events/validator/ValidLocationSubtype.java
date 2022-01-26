package org.dcsa.core.events.validator;

import org.dcsa.core.events.model.transferobjects.LocationTO;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validates that a given location matches a given valid subtype
 *
 * Example subtypes could be a facility, a geo coordinate, or an
 * address-based location as three distinct subtypes.
 *
 * The validation ensures a minimum requirement, but it does not restrict
 * additional fields.  As an example, the {@link LocationSubtype#GEO_COORDINATE}
 * subtype ensures that the "latitude" and "longitude" field must be non-null.
 * However, it does not <i>forbid</i> the address field from being present as
 * well.
 *
 * The annotation can be repeated to require that the location satisfies multiple
 * location subtypes at the same time (same as logical AND) in the rare case where
 * that is needed.
 *
 * Note that this does <b>not</b> ensure that the location is not null.
 * For that, please also use @{@link javax.validation.constraints.NotNull}
 * in addition to this annotation.
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = ValidLocationSubtypeValidator.class)
@Repeatable(ValidLocationSubtype.List.class)
public @interface ValidLocationSubtype {

    /**
     * List of subtypes that are valid
     *
     * Defaults to {@link LocationSubtype#values()} when empty.
     */
    LocationSubtype[] anyOf() default {};

    enum LocationSubtype {
        /**
         * Ensures that UN Location Code is not null
         */
        UN_LOCATION,
        /**
         * Ensures that the Geo coordinate fields are set (latitude and longitude)
         */
        GEO_COORDINATE,
        /**
         * Ensures that the facility fields (facilityCode and facilityCodeListProvider) is set
         *
         * It may also ensure that the UN Location Code is also set (depending on the code list provider)
         */
        FACILITY,
        /**
         * Ensures that address is not null.
         *
         * There are no restrictions on the {@link org.dcsa.core.events.model.Address} object other
         * than it is not null. Accordingly, you will need an additional validation if you expect
         * particular fields set on the Address.
         */
        ADDRESS,
        ;

        public boolean locationMatch(LocationTO locationTO) {
            switch (this) {
                case UN_LOCATION:
                    return locationTO.getUnLocationCode() != null;
                case GEO_COORDINATE:
                    return locationTO.getLatitude() != null && locationTO.getLongitude() != null;
                case FACILITY:
                    // At the moment, a facility implies a UN location code as existing facility code lists
                    // are defined "on top" of UN location codes.  However, eventually we may get a code list
                    // provider not defined by UN location code.  In that case this conditional should be
                    // updated to cater for that.
                    return locationTO.getUnLocationCode() != null && locationTO.getFacilityCode() != null && locationTO.getFacilityCodeListProvider() != null;
                case ADDRESS:
                    return locationTO.getAddress() != null;
            }
            throw new AssertionError("Unhandled enum case");
        }
    }

    String message() default "must match one of the following location sub types: {anyOf}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        ValidLocationSubtype[] value();
    }
}
