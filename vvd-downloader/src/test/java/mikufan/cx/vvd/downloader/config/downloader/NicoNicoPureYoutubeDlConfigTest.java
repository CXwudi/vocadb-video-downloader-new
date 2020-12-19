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
@SpringBootTest(properties = {
    //in test case, resources folder src/test/resources/ is also our base directory
    "downloader.io.input-directory=2019年V家新曲-测试用",
    "downloader.io.output-directory=dsa",
    "downloader.config.nico-pure-youtube-dl.youtube-dl-path=dummy.exe",
    "downloader.config.youtube.youtube-dl-path=dummy.exe",
    "downloader.config.youtube.ffmpeg-path=dummy.exe",
})
class NicoNicoPureYoutubeDlConfigTest {

  @Autowired private NicoNicoPureYoutubeDlConfig config;

  @Test
  void testPrint(){
    log.info("config = {}", config);
  }
}