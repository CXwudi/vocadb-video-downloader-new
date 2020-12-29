package mikufan.cx.vvd.common.validation.validator;

import mikufan.cx.vvd.common.exception.RuntimeVocaloidException;
import mikufan.cx.vvd.common.validation.annotation.PathsNotSame;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * @author CX无敌
 * @date 2020-12-26
 */
public class ValidatePathsNotSameValidator implements ConstraintValidator<PathsNotSame, Object> {

  private String[] fields;

  @Override
  public void initialize(PathsNotSame constraintAnnotation) {
    fields = constraintAnnotation.fields();
  }

  @Override
  public boolean isValid(Object object, ConstraintValidatorContext context) {

    if (fields != null && fields.length > 0){
      return realValidate(object, context);
    } else {
      throw new RuntimeVocaloidException("Fields are empty, this shouldn't happened");
    }
  }

  private boolean realValidate(Object object, ConstraintValidatorContext context) {
    context.disableDefaultConstraintViolation();
    var paths = new ArrayList<Path>(fields.length);

    for (var field : fields){
      var path = getPath(object, field, context);
      paths.add(path);
    }

    for (int i = 0; i < fields.length; i++) {
      var path1 = paths.get(i);
      var field1 = fields[i];
      for (int j = i + 1; j < fields.length; j++) {
        var path2 = paths.get(j);
        var field2 = fields[j];
        if (path1 == null && path2 == null){
          context.buildConstraintViolationWithTemplate(String.format("fields \"%s\" and \"%s\" can not be same path as null path", field1, field2))
              .addPropertyNode(field1)
              .addPropertyNode(field2)
              .addConstraintViolation();
          return false;
        } else if (path1 != null && path1.equals(path2)){
          context.buildConstraintViolationWithTemplate(String.format("fields \"%s\" and \"%s\" can not be same path of %s", field1, field2, path1))
              .addPropertyNode(field1)
              .addPropertyNode(field2)
              .addConstraintViolation();
          return false;
        }
      }
    }

    return true;
  }


  private Path getPath(Object object, String fieldName, ConstraintValidatorContext context){
    Path path = null;
    try {
      var field = object.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      var rawObjectPath = field.get(object);
      if (rawObjectPath instanceof Path){
        path = (Path) rawObjectPath;
      } else if (rawObjectPath instanceof File){
        path = ((File) rawObjectPath).toPath();
      } else {
        context.buildConstraintViolationWithTemplate(String.format("The field is not a path: %s", fieldName))
            .addPropertyNode(fieldName)
            .addConstraintViolation();
      }
    } catch (IllegalAccessException e) {
      context.buildConstraintViolationWithTemplate(String.format("Can not access field: %s, %s", fieldName, e.getMessage()))
          .addPropertyNode(fieldName)
          .addConstraintViolation();
      return null;
    } catch (NoSuchFieldException e) {
      context.buildConstraintViolationWithTemplate(String.format("Can not find field: %s", fieldName))
          .addPropertyNode(fieldName)
          .addConstraintViolation();
      return null;
    }

    if (path == null){
      // if the path itself is null
      return null;
    } else {
      return path.normalize().toAbsolutePath();
    }
  }
}
