package mikufan.cx.vvd.downloader.config.downloader;

import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.downloader.util.TestEnvHolder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Charles Chen 101035684
 * @date 2020-12-18
 */
@Slf4j
class NicoPureYoutubeDlConfigTest extends TestEnvHolder {

  @Autowired private NicoPureYoutubeDlConfig config;

  @Test
  void testPrint(){
    log.info("config = {}", config);
  }
}