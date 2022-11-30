package mikufan.cx.vvd.extractor.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;
import mikufan.cx.vvd.common.label.VSongResource;
import mikufan.cx.vvd.common.label.ValidationPhase;
import mikufan.cx.vvd.common.vocadb.model.SongForApi;
import mikufan.cx.vvd.extractor.service.extractor.AudioExtractor;
import mikufan.cx.vvd.extractor.service.tagger.AudioTagger;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * hold any return valued and parameters needed for {@link mikufan.cx.vvd.extractor.service.MainService}
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
  // no need to put audio file name as song info + ext is enough

  @NotNull(groups = ValidationPhase.Three.class) AudioTagger audioTagger;

  ExtractStatus currentExtractStatus;

}
