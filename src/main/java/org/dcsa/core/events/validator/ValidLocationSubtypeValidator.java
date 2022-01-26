package org.dcsa.core.events.validator;

import org.dcsa.core.events.model.transferobjects.LocationTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public class ValidLocationSubtypeValidator implements ConstraintValidator<ValidLocationSubtype, LocationTO> {

    private Set<ValidLocationSubtype.LocationSubtype> expectedSubtypes = EnumSet.allOf(ValidLocationSubtype.LocationSubtype.class);

    @Override
    public void initialize(ValidLocationSubtype constraintAnnotation) {
        expectedSubtypes = EnumSet.copyOf(Arrays.asList(constraintAnnotation.anyOf()));
    }

    @Override
    public boolean isValid(LocationTO value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        for (ValidLocationSubtype.LocationSubtype subtype : this.expectedSubtypes) {
            if (subtype.locationMatch(value)) {
                return true;
            }
        }
        return false;
    }
}
