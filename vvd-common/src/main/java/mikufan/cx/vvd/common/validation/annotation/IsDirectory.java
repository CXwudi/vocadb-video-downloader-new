package mikufan.cx.vvd.common.validation.annotation;

import mikufan.cx.vvd.common.validation.validator.ValidDirectoryPathValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * test that a {@link java.nio.file.Path} is a valid existing directory
 * @author CX无敌
 * @date 2020-12-19
 */
@Constraint(validatedBy = ValidDirectoryPathValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
@Documented
public @interface IsDirectory {
  String message() default "Invalid directory";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  /**
   * {@code true} means skip the check if the directory is null or doesn't exist
   */
  boolean optionalCheck() default false;

  boolean checkReadable() default true;

  boolean checkWritable() default false;

  boolean checkExecutable() default true;
}
