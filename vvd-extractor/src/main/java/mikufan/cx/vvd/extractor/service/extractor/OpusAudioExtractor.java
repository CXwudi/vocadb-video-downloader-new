package mikufan.cx.vvd.extractor.service.extractor;

import java.nio.file.Path;

/**
 * @author CX无敌
 * @date 2020-12-29
 */
public class OpusAudioExtractor implements AudioExtractor {
  @Override
  public String getName() {
    return "Opus Audio Extractor by ffmpeg only";
  }

  @Override
  public ExtractStatus extractAudio(Path pv, Path directory, String fileName) throws InterruptedException {
    return null;
  }
}
