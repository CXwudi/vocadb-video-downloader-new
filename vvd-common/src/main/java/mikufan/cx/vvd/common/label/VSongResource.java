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

  @NotBlank String pvService;
  @NotBlank String pvId;
  @NotBlank String pvUrl;
}
