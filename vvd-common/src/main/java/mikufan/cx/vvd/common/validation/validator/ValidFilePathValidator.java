package mikufan.cx.vvd.common.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import mikufan.cx.vvd.common.validation.annotation.IsFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @author CX无敌
 * @date 2020-12-19
 */
public class ValidFilePathValidator implements ConstraintValidator<IsFile, Path> {

  private boolean optionalCheck;

  private boolean checkReadable;

  private boolean checkWritable;

  private boolean checkExecutable;

  @Override
  public void initialize(IsFile constraintAnnotation) {
    optionalCheck = constraintAnnotation.optionalCheck();
    checkReadable = constraintAnnotation.checkReadable();
    checkWritable = constraintAnnotation.checkWritable();
    checkExecutable = constraintAnnotation.checkExecutable();
  }

  @Override
  public boolean isValid(Path path, ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();
    if (Objects.isNull(path)){
      if (optionalCheck){
        return true;
      } else {
        context.buildConstraintViolationWithTemplate("The path can not be null")
            .addConstraintViolation();
        return false;
      }
    }
    var fullPath = path.toAbsolutePath().toString();
    if (!Files.exists(path)){
      if (optionalCheck){
        return true;
      } else {
        context.buildConstraintViolationWithTemplate(
            String.format("The path '%s' doesn't exist", fullPath))
            .addConstraintViolation();
        return false;
      }
    } else if (!Files.isRegularFile(path)){
      context.buildConstraintViolationWithTemplate(
          String.format("The path '%s' is not a file", fullPath))
          .addConstraintViolation();
      return false;
    } else {
      // check privilege
      if (checkReadable && !Files.isReadable(path)){
        context.buildConstraintViolationWithTemplate(
            String.format("The file '%s' is not readable", fullPath))
            .addConstraintViolation();
        return false;
      }
      if (checkWritable && !Files.isWritable(path)){
        context.buildConstraintViolationWithTemplate(
            String.format("The file '%s' is not writable", fullPath))
            .addConstraintViolation();
        return false;
      }
      if (checkExecutable && !Files.isExecutable(path)){
        context.buildConstraintViolationWithTemplate(
            String.format("The file '%s' is not executable", fullPath))
            .addConstraintViolation();
        return false;
      }
      return true;
    }
  }
}
