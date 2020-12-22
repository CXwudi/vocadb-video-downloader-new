package mikufan.cx.vvd.common.validation.annotation;

import mikufan.cx.vvd.common.validation.validator.ValidateSupportPvServicesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author CX无敌
 * @date 2020-12-21
 */
@Constraint(validatedBy = ValidateSupportPvServicesValidator.class)
@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
@Documented
public @interface IsSupportedPvServices {
  String message() default "List containing unsupported pv services";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
