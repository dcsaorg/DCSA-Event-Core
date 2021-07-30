package org.dcsa.core.events.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.stream.Stream;

public class EnumSubsetValidator implements ConstraintValidator<EnumSubset, String> {

  private String[] subsetTypes;

  @Override
  public void initialize(EnumSubset constraintAnnotation) {
    this.subsetTypes = constraintAnnotation.anyOf();
  }

  @Override
  public boolean isValid(String types, ConstraintValidatorContext constraintValidatorContext) {

    if (null == types) {
      return true;
    }
    Stream<String> enumStream =
        types.contains(",") ? Arrays.stream(types.split(",")) : Stream.of(types);

    return enumStream.allMatch(e -> Arrays.asList(subsetTypes).contains(e));
  }
}
