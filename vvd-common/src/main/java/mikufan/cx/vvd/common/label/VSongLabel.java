package mikufan.cx.vvd.common.label;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.Payload;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mutable class as the metadata of a song used across the whole vvd
 *
 * @author CX无敌
 * @date 2021-05-29
 */
@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@Jacksonized
@FieldDefaults(level = AccessLevel.PROTECTED)
@HasRequiredResource(groups = ValidationPhase.Two.class)
public class VSongLabel {

  /**
   * the file name of this label itself <br/>
   * we need the label name recorded in the label itself
   * as various VSong naming methods are not globally available anymore
   */
  @NotBlank(groups = ValidationPhase.One.class) String labelFileName;
  @NotBlank(groups = ValidationPhase.One.class) String infoFileName;
  @Min(value = 1, groups = ValidationPhase.One.class) long order;

  // these three will be validated by @HasRequiredResource

  String pvFileName;
  String audioFileName;
  String thumbnailFileName;

  // we need to record this because the audio format is depended on file formats downloaded from pv service
  // careful that youtube can download either .mp4 or .mkv depended on if ffmpeg exists
  // for .mkv, the audio format is undetectable without using mediainfo exe
  @NotBlank(groups = ValidationPhase.Two.class) String pvService;
  @NotBlank(groups = ValidationPhase.Two.class) String pvId;
  @NotBlank(groups = ValidationPhase.Two.class) String pvUrl;

}

/**
 * To check {@link VSongLabel} has validate resources. <br/>
 * Which means has either {@link VSongLabel#pvFileName} or {@link VSongLabel#audioFileName} non-empty and
 * has {@link VSongLabel#thumbnailFileName} non-empty.
 * @author CX无敌
 */
@Constraint(validatedBy = HasRequiredResourceValidator.class)
@Target({ TYPE })
@Retention(RUNTIME)
@Documented
@interface HasRequiredResource {

  String message() default "Doesn't contain required resources";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

class HasRequiredResourceValidator implements ConstraintValidator<HasRequiredResource, VSongLabel> {

  @Override
  public boolean isValid(VSongLabel value, javax.validation.ConstraintValidatorContext context) {
    return StringUtils.isNotBlank(value.getPvFileName()) || StringUtils.isNotBlank(value.getAudioFileName())
        && StringUtils.isNotBlank(value.getThumbnailFileName());
  }
}