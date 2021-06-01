package mikufan.cx.vvd.common.label;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

/**
 * Mutable class as the metadata for the whole vvd
 * @author CX无敌
 * @date 2021-05-29
 */
@Data @AllArgsConstructor
@Builder(toBuilder = true) @Jacksonized
@FieldDefaults(level = AccessLevel.PROTECTED)
public class VSongLabel {

  @NotBlank(groups = ValidationPhase.One.class) String infoFileName;

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
