package mikufan.cx.vvd.common.validation.validator;

import mikufan.cx.vvd.common.exception.RuntimeVocaloidException;
import mikufan.cx.vvd.common.validation.annotation.PathsNotSame;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
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
      if (path == null){
        return false;
      } else {
        paths.add(path);
      }
    }

    for (int i = 0; i < fields.length; i++) {
      var path1 = paths.get(i);
      var field1 = fields[i];
      for (int j = i + 1; j < fields.length; j++) {
        var path2 = paths.get(j);
        var field2 = fields[j];

        if (path1.equals(path2)){
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
      path = (Path) field.get(object);
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
    return path.normalize().toAbsolutePath();
  }
}
