package mikufan.cx.vvd.extractor.service.extractor;

import mikufan.cx.vvd.extractor.config.IOConfig;
import mikufan.cx.vvd.extractor.util.TestEnvHolder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author CX无敌
 * @date 2020-12-29
 */
@Disabled
class YoutubeOpusAudioExtractorTest extends TestEnvHolder {

  @Autowired
  private YoutubeOpusAudioExtractor extractor;

  @Autowired
  private IOConfig ioConfig;


  @Test
  void testExtract() throws InterruptedException {
    var extractStatus = extractor.extractAudio(
        Path.of("D:\\11134\\Videos\\Vocaloid Coding POC\\Project VD test\\2019年V家新曲 sample PVs 2"
            , "【初音ミク】glare【kz】-pv.mkv"),
        ioConfig.getOutputDirectory(),
        "【初音ミク】glare【kz】-audio.ogg"
    );
    assertTrue(extractStatus.isSucceed());
  }
}