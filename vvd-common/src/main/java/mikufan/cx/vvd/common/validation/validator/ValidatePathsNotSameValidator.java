package mikufan.cx.vvd.common.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException;
import mikufan.cx.vvd.common.validation.annotation.PathsNotSame;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

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
    var hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
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
          hibernateContext
              .addMessageParameter("field1", field1)
              .addMessageParameter("field2", field2)
              .buildConstraintViolationWithTemplate("fields \"{field1}\" and \"{field2}\" can not be same path as null path")
              .addPropertyNode(field1)
              .addPropertyNode(field2)
              .addConstraintViolation();
          return false;
        } else if (path1 != null && path1.equals(path2)){
          hibernateContext
              .addMessageParameter("field1", field1)
              .addMessageParameter("field2", field2)
              .addMessageParameter("path", String.valueOf(path1))
              .buildConstraintViolationWithTemplate("fields \"{field1}\" and \"{field2}\" can not be same path of {path}")
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
    var hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
    Path path = null;
    try {
      var field = object.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      var rawObjectPath = field.get(object);
      if (rawObjectPath instanceof Path fieldPath){
        path = fieldPath;
      } else if (rawObjectPath instanceof File fieldFile){
        path = fieldFile.toPath();
      } else {
        hibernateContext
            .addMessageParameter("fieldName", fieldName)
            .buildConstraintViolationWithTemplate("The field is not a path: {fieldName}")
            .addPropertyNode(fieldName)
            .addConstraintViolation();
      }
    } catch (IllegalAccessException e) {
      hibernateContext
          .addMessageParameter("fieldName", fieldName)
          .addMessageParameter("error", e.getMessage())
          .buildConstraintViolationWithTemplate("Can not access field: {fieldName}, {error}")
          .addPropertyNode(fieldName)
          .addConstraintViolation();
      return null;
    } catch (NoSuchFieldException e) {
      hibernateContext
          .addMessageParameter("fieldName", fieldName)
          .buildConstraintViolationWithTemplate("Can not find field: {fieldName}")
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
