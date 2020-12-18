package mikufan.cx.vvd.downloader.config.downloader;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Charles Chen 101035684
 * @date 2020-12-18
 */
@Slf4j
@SpringBootTest(properties = "downloader.config.nico-pure-youtube-dl.youtube-dl-options.--username=cxwudi")
class NicoNicoPureYoutubeDlConfigTest {

  @Autowired private NicoNicoPureYoutubeDlConfig config;

  @Test
  void testPrint(){
    log.info("config = {}", config);
  }
}