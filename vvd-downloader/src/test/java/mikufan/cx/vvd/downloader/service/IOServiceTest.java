package mikufan.cx.vvd.downloader.service;

import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.downloader.service.downloader.DownloadStatus;
import mikufan.cx.vvd.downloader.util.TestEnvHolder;
import org.junit.jupiter.api.Disabled;
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

  @Test @Disabled
  void testWrite(){
    var allSongsToBeDownloaded = ioService.getAllSongsToBeDownloadedInOrder();
    var samples1 = allSongsToBeDownloaded.subList(0, 20);
    var samples2 = allSongsToBeDownloaded.subList(20, 30);
    samples1.forEach(sample -> {
      ioService.recordDownloadedSong(DownloadStatus.success(), sample);
    });
    samples2.forEach(sample -> {
      ioService.recordDownloadedSong(DownloadStatus.failure("Sample error"), sample);
    });
  }
}