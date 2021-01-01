package mikufan.cx.vvd.extractor.service.tagger;

import mikufan.cx.vvd.extractor.util.TestEnvHolder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author CX无敌
 * @date 2020-12-31
 */
@Disabled
class YoutubeOpusAudioTaggerTest extends TestEnvHolder {

  @Autowired
  private YoutubeOpusAudioTagger tagger;

  @Test
  void testTagging() throws InterruptedException {
    var inputDir = Path.of("D:\\11134\\Videos\\Vocaloid Coding POC\\Project VD test\\2019年V家新曲 sample PVs 2");
    var outputDir = Path.of("D:\\11134\\Videos\\Vocaloid Coding POC\\Project VD test\\2019年V家新曲 sample audios 2");

    var extractStatus = tagger.handleTagging(
        outputDir.resolve("【初音ミク】glare【kz】-audio.ogg"),
        inputDir.resolve("【初音ミク】glare【kz】-thumbnail.webp"),
        inputDir.resolve("【初音ミク】glare【kz】-resource.json"),
        inputDir.resolve("【初音ミク】glare【kz】-songInfo.json")
    );
    assertTrue(extractStatus.isSucceed());
  }
}