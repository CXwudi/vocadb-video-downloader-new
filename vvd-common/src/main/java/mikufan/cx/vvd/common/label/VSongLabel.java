package mikufan.cx.vvd.common.label;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

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
public class VSongLabel {

  /**
   * the file name of this label itself <br/>
   * we need the label name recorded in the label itself
   * as various VSong naming methods are not globally available anymore
   */
  @NotBlank(groups = ValidationPhase.One.class) String labelFileName;
  @NotBlank(groups = ValidationPhase.One.class) String infoFileName;
  @Min(value = 1, groups = ValidationPhase.One.class) long order;

  @NotBlank(groups = ValidationPhase.Two.class) String pvFileName;
  String audioFileName;
  @NotBlank(groups = ValidationPhase.Two.class) String thumbnailFileName;

  // we need to record this because the audio format is depended on file formats downloaded from pv service
  // careful that youtube can download either .mp4 or .mkv depended on if ffmpeg exists
  // for .mkv, the audio format is undetectable without using mediainfo exe
  @NotBlank(groups = ValidationPhase.Two.class) String pvService;
  @NotBlank(groups = ValidationPhase.Two.class) String pvId;
  @NotBlank(groups = ValidationPhase.Two.class) String pvUrl;

}
