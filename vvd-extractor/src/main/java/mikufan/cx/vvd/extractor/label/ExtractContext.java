package mikufan.cx.vvd.extractor.label;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;
import mikufan.cx.vvd.common.label.VSongResource;
import mikufan.cx.vvd.common.vocadb.model.SongForApi;
import mikufan.cx.vvd.extractor.service.extractor.AudioExtractor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * @author CX无敌
 * @date 2020-12-19
 */
@Getter @ToString
@Builder(toBuilder = true) @Jacksonized
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class ExtractContext {

  @NotNull @Valid VSongResource songResource;
  @NotNull(groups = ValidationPhase.One.class) SongForApi songInfo;

  @NotNull(groups = ValidationPhase.Two.class) AudioExtractor audioExtractor;
  @NotBlank(groups = ValidationPhase.Two.class) String audioExtension;


  ExtractStatus currentExtractStatus;

}
