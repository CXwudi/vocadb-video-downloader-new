package mikufan.cx.vvd.downloader.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author CX无敌
 * @date 2020-12-19
 */
@Slf4j
@SpringBootTest(properties = {
    "downloader.io.input-directory=src/test/resources/2019年V家新曲-测试用",
    "downloader.io.output-directory=dsa",
    "downloader.config.nico-pure-youtube-dl.youtube-dl-path=dummy.exe",
    "downloader.config.youtube.youtube-dl-path=dummy.exe",
    "downloader.config.youtube.ffmpeg-path=dummy.exe",
})
class IOManagerTest {

  @Autowired
  private IOManager ioManager;

  @Test
  void testRead(){
    var allSongsToBeDownloaded = ioManager.getAllSongsToBeDownloadedInOrder();
  }
}