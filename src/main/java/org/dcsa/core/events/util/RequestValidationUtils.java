package org.dcsa.core.events.util;

import org.apache.commons.lang3.EnumUtils;
import org.dcsa.core.events.exception.BadRequestException;

import java.util.stream.Stream;


/**
 * Validations methods
 */
public abstract class RequestValidationUtils {

  protected void validMaxLength(int max, int actual) {
    if (actual > max) {
      throw new BadRequestException();
    }
  }

  protected <T extends Enum<T>> void checkIfValidEnumAndPartOf(
      String s, Class<T> clazz, Stream<T> es) {
    if (!EnumUtils.isValidEnum(clazz, s) && es.noneMatch(e -> s.equals(e.toString()))) {
      throw new BadRequestException();
    }
  }

  protected <T extends Enum<T>> void checkIfValidEnum(String s, Class<T> clazz) {
    if (!EnumUtils.isValidEnum(clazz, s)) {
      throw new BadRequestException();
    }
  }

  protected void checkIfValidSize(int min, int max, int actual) {
    if (actual >= min && actual <= max) {
      throw new BadRequestException();
    }
  }
}
