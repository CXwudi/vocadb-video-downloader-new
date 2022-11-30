package mikufan.cx.vvd.common.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import mikufan.cx.vvd.common.validation.validator.ValidatePathsNotSameValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A class-level annotation for checking declared {@link java.nio.file.Path} fields are not same
 * @author CX无敌
 * @date 2020-12-26
 */
@Constraint(validatedBy = ValidatePathsNotSameValidator.class)
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
public @interface PathsNotSame {

  String message() default "Paths can not be same";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  /**
   * array of field name of your class, fields must all be {@link java.nio.file.Path} type
   */
  String[] fields();

}
