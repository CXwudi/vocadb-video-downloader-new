package mikufan.cx.vvd.common.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import mikufan.cx.vvd.common.validation.validator.ValidFilePathValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * test that a {@link java.nio.file.Path} is a valid existing file
 * @author CX无敌
 * @date 2020-12-19
 */
@Constraint(validatedBy = ValidFilePathValidator.class)
@Target({ FIELD,METHOD,PARAMETER })
@Retention(RUNTIME)
@Documented
public @interface IsFile {
  String message() default "Invalid file";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  /**
   * {@code true} means skip the check if the file is null or doesn't exist
   */
  boolean optionalCheck() default false;

  boolean checkReadable() default true;

  boolean checkWritable() default false;

  boolean checkExecutable() default true;
}
