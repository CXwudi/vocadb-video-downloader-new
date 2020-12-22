package mikufan.cx.vvd.downloader.service;

import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.util.PvServiceStr;
import mikufan.cx.vvd.downloader.util.TestEnvHolder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author CX无敌
 * @date 2020-12-21
 */
@Slf4j
class DownloaderDeciderImplTest extends TestEnvHolder {

  @Autowired
  private DownloaderDecider downloaderDecider;

  @Test
  void testGet(){
    var suitableDownloader = downloaderDecider.getSuitableDownloader(PvServiceStr.NICONICO);
    log.info("suitableDownloader = {}", suitableDownloader.getClass().getSimpleName());
  }
}