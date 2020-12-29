package mikufan.cx.vvd.extractor.util;

import lombok.Builder;
import lombok.Value;
import mikufan.cx.vvd.extractor.service.extractor.AudioExtractor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author CX无敌
 * @date 2020-12-29
 */
@Value @Builder
public class ExtractorInfo {

  @NotNull AudioExtractor audioExtractor;
  @NotBlank String audioExtension;

}
