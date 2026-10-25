package com.eng1c4t3.goosesurvivor.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

public class ValidatorUtil {
  private static final ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();
  private static final Validator VALIDATOR = FACTORY.getValidator();

  private ValidatorUtil() {}

  public static void close() {
    FACTORY.close();
  }

  public static void validate(Object object) {
    Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(object);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }
}
