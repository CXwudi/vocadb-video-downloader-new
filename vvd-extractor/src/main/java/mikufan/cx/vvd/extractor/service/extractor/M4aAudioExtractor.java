package mikufan.cx.vvd.extractor.service.extractor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

/**
 * @author CX无敌
 * @date 2020-12-29
 */
@Service @Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class M4aAudioExtractor implements AudioExtractor {


  @Override
  public String getName() {
    return "M4a Audio Extractor by ffmpeg only";
  }

  @Override
  public ExtractStatus extractAudio(Path pv, Path directory, String fileName) throws InterruptedException {
    return null;
  }
}
