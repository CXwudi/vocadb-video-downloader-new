package mikufan.cx.vvd.extractor.service.tagger;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.extractor.label.ExtractStatus;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

/**
 * @author CX无敌
 * @date 2020-12-31
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class YoutubeOpusAudioTagger implements AudioTagger {


  @Override
  public String getName() {
    return "Opus Audio Tagger by python mutagen";
  }

  @Override
  public ExtractStatus handleTagging(Path audioFile, Path thumbnailFile, Path resourceFile, Path infoFile) throws InterruptedException {
    return null;
  }
}
