package mikufan.cx.vvd.extractor.service.tagger;

import mikufan.cx.vvd.extractor.util.TestEnvHolder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;

/**
 * @author CX无敌
 * @date 2020-12-31
 */
@Disabled
class NiconicoM4aAudioTaggerTest extends TestEnvHolder {

  @Autowired
  private NiconicoM4aAudioTagger tagger;

  @Test
  void testTagging() throws InterruptedException {
    var inputDir = Path.of("D:\\11134\\Videos\\Vocaloid Coding POC\\Project VD test\\2019年V家新曲 sample PVs 2");
    var outputDir = Path.of("D:\\11134\\Videos\\Vocaloid Coding POC\\Project VD test\\2019年V家新曲 sample audios 2");

    tagger.handleTagging(
        outputDir.resolve("【初音ミク, 鏡音リン】リングの熾天使【Mitchie M】-audio.m4a"),
        inputDir.resolve("【初音ミク, 鏡音リン】リングの熾天使【Mitchie M】-thumbnail.jpg"),
        inputDir.resolve("【初音ミク, 鏡音リン】リングの熾天使【Mitchie M】-resource.json"),
        inputDir.resolve("【初音ミク, 鏡音リン】リングの熾天使【Mitchie M】-songInfo.json")
    );
  }
}