package mikufan.cx.vvd.extractor.service.tagger;

import mikufan.cx.vvd.extractor.util.TestEnvHolder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author CX无敌
 * @date 2020-12-31
 */
@Disabled
class NiconicoM4aAudioTaggerTest extends TestEnvHolder {

  @Autowired
  private NiconicoM4aAudioTagger tagger;

  @Test
  void testTagging() throws InterruptedException {
    // WARNING: The PC doesn't have python in windows, need to run this test in wsl environment
    var inputDir = Path.of("/mnt/d/coding-workspace/Vocaloid Coding POC/Project VD test/2019年V家新曲 sample PVs 2");
    var outputDir = Path.of("/mnt/d/coding-workspace/Vocaloid Coding POC/Project VD test/2019年V家新曲 sample audios 2");

    var extractStatus = tagger.handleTagging(
        outputDir.resolve("【初音ミク, 鏡音リン】リングの熾天使【Mitchie M】-audio.m4a"),
        inputDir.resolve("【初音ミク, 鏡音リン】リングの熾天使【Mitchie M】-thumbnail.jpg"),
        inputDir.resolve("【初音ミク, 鏡音リン】リングの熾天使【Mitchie M】-resource.json"),
        inputDir.resolve("【初音ミク, 鏡音リン】リングの熾天使【Mitchie M】-songInfo.json")
    );
    assertTrue(extractStatus.isSucceed());
  }
}