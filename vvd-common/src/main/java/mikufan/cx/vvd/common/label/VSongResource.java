package mikufan.cx.vvd.common.label;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

/**
 * This class is used for recording all files and other info related to the downloaded VSong,
 * assuming all files are in same folder
 * @author CX无敌
 * @date 2020-12-31
 */
@Getter @ToString
@Builder(toBuilder = true) @Jacksonized
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class VSongResource {

  @NotBlank String pvFileName;
  String audioFileName;
  @NotBlank String thumbnailFileName;
  @NotBlank String infoFileName;

  // we need to record this because the audio format is depended on file formats downloaded from pv service
  // careful that youtube can download either .mp4 or .mkv depended on if ffmpeg exists
  // for .mkv, the audio format is undetectable without using mediainfo exe
  @NotBlank String pvService;
  @NotBlank String pvId;
  @NotBlank String pvUrl;
}
