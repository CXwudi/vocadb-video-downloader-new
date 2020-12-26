package mikufan.cx.vvd.common.validation.annotation;

import mikufan.cx.vvd.common.validation.validator.ValidatePathsNotSameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
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
