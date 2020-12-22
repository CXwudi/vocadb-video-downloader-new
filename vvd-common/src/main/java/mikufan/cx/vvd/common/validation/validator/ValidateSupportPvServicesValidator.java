package mikufan.cx.vvd.common.validation.validator;

import mikufan.cx.vvd.common.util.PvService;
import mikufan.cx.vvd.common.validation.annotation.IsSupportedPvServices;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * @author CX无敌
 * @date 2020-12-21
 */
public class ValidateSupportPvServicesValidator implements ConstraintValidator<IsSupportedPvServices, List<String>> {
  @Override
  public boolean isValid(List<String> value, ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();

    if (CollectionUtils.isEmpty(value)){
      context.buildConstraintViolationWithTemplate("The list can not be null or empty")
          .addConstraintViolation();
      return false;
    } else if (value.size() != PvService.ALL_SUPPORTED_SERVICES.size()){
      context.buildConstraintViolationWithTemplate(
          String.format("The list must contains all %s of %s",
              PvService.ALL_SUPPORTED_SERVICES.size(), PvService.ALL_SUPPORTED_SERVICES))
          .addConstraintViolation();
      return false;
    } else {
      for (var service : value){
        if (!PvService.ALL_SUPPORTED_SERVICES.contains(service)){
          context.buildConstraintViolationWithTemplate(
              String.format("The preference list contains a yet unsupported pv service, %s", service))
              .addConstraintViolation();
          return false;
        }
      }
      return true;
    }
  }
}
