package mikufan.cx.vvd.common.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import mikufan.cx.vvd.common.validation.annotation.IsFile;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

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
    var hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    if (Objects.isNull(path)){
      if (optionalCheck){
        return true;
      } else {
        hibernateContext.buildConstraintViolationWithTemplate("The path can not be null")
            .addConstraintViolation();
        return false;
      }
    }
    var fullPath = path.toAbsolutePath().toString();
    if (!Files.exists(path)){
      if (optionalCheck){
        return true;
      } else {
        hibernateContext
            .addMessageParameter("path", fullPath)
            .buildConstraintViolationWithTemplate("The path '{path}' doesn't exist")
            .addConstraintViolation();
        return false;
      }
    } else if (!Files.isRegularFile(path)){
      hibernateContext
          .addMessageParameter("path", fullPath)
          .buildConstraintViolationWithTemplate("The path '{path}' is not a file")
          .addConstraintViolation();
      return false;
    } else {
      // check privilege
      if (checkReadable && !Files.isReadable(path)){
        hibernateContext
            .addMessageParameter("path", fullPath)
            .buildConstraintViolationWithTemplate("The file '{path}' is not readable")
            .addConstraintViolation();
        return false;
      }
      if (checkWritable && !Files.isWritable(path)){
        hibernateContext
            .addMessageParameter("path", fullPath)
            .buildConstraintViolationWithTemplate("The file '{path}' is not writable")
            .addConstraintViolation();
        return false;
      }
      if (checkExecutable && !Files.isExecutable(path)){
        hibernateContext
            .addMessageParameter("path", fullPath)
            .buildConstraintViolationWithTemplate("The file '{path}' is not executable")
            .addConstraintViolation();
        return false;
      }
      return true;
    }
  }
}
