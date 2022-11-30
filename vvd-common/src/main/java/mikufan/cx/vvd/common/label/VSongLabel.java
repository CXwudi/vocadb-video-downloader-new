package mikufan.cx.vvd.common.label;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.StringUtils;

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
  /**
   * order value always start at 1, not the programmer's 0-based index
   */
  @Positive(groups = ValidationPhase.One.class) long order;

  // these three will be validated by @HasRequiredResource
  // mediainfo will be used in vvd-extractor to accurately determine the audio format
  String pvFileName;
  String audioFileName;
  String thumbnailFileName;

  /**
   * need the VocaDB ID of the pv so that we know which PV is successfully downloaded
   * <p>
   * the VocaDB PV ID should always be positive.
   */
  @Positive(groups = ValidationPhase.Two.class) int pvVocaDbId;
  // this is recorded for reference
  @NotBlank(groups = ValidationPhase.Two.class) String downloaderName;

  /**
   * the name of the final output audio file that has thumbnail and tags properly added
   */
  @NotBlank(groups = ValidationPhase.Three.class) String processedAudioFileName;

}

/**
 * To check {@link VSongLabel} has validate resources. <br/>
 * Which means has either {@link VSongLabel#pvFileName} or {@link VSongLabel#audioFileName} non-empty and
 * has {@link VSongLabel#thumbnailFileName} non-empty. <br/>
 * However, this doesn't check if the file is really exists, because we want the program to freely use any location
 * as the root folder to store these resources.
 *
 * @author CX无敌
 */
@Constraint(validatedBy = HasRequiredResourceValidator.class)
@Target({TYPE})
@Retention(RUNTIME)
@Documented
@interface HasRequiredResource {

  String message() default "Doesn't contain required resources";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

class HasRequiredResourceValidator implements ConstraintValidator<HasRequiredResource, VSongLabel> {

  @Override
  public boolean isValid(VSongLabel value, jakarta.validation.ConstraintValidatorContext context) {
    var hasMedia = StringUtils.isNotBlank(value.getPvFileName()) || StringUtils.isNotBlank(value.getAudioFileName());
    var hasThumbnail = StringUtils.isNotBlank(value.getThumbnailFileName());
    var result = hasMedia && hasThumbnail;
    if (!result) {
      context.disableDefaultConstraintViolation();
      if (!hasMedia) {
        context.buildConstraintViolationWithTemplate("%s must contain at least one of a PV file or an audio file".formatted(value.infoFileName))
            .addPropertyNode("pvFileName")
            .addPropertyNode("audioFileName")
            .addConstraintViolation();
      }

      if (!hasThumbnail) {
        context.buildConstraintViolationWithTemplate("%s must contain a thumbnail file".formatted(value.infoFileName))
            .addPropertyNode("thumbnailFileName")
            .addConstraintViolation();
      }
    }
    return result;
  }
}