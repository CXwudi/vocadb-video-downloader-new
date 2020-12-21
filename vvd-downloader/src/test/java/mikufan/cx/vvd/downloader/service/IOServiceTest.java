package mikufan.cx.vvd.downloader.service;

import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.downloader.util.TestEnvHolder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author CX无敌
 * @date 2020-12-19
 */
@Slf4j
class IOServiceTest extends TestEnvHolder {

  @Autowired
  private IOService ioService;

  @Test
  void testRead(){
    var allSongsToBeDownloaded = ioService.getAllSongsToBeDownloadedInOrder();
  }
}