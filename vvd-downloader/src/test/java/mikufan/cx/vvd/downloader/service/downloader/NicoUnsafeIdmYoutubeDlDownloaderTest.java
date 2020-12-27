package mikufan.cx.vvd.downloader.service.downloader;

import mikufan.cx.vvd.downloader.util.TestEnvHolder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author CX无敌
 * @date 2020-12-27
 */
@SpringBootTest(properties = {
    "config.downloader.nico-unsafe-idm-youtube-dl.youtube-dl-path=D:/11134/Videos/Vocaloid Coding POC/youtube-dl-niconico-enhanced.exe",
    "config.downloader.nico-unsafe-idm-youtube-dl.youtube-dl-options.--cookies=D:/11134/Videos/Vocaloid Coding POC/niconico cookies.txt",
    "config.downloader.nico-unsafe-idm-youtube-dl.idm-path=C:/Program Files (x86)/Internet Download Manager/IDMan.exe"
})
@Disabled
class NicoUnsafeIdmYoutubeDlDownloaderTest extends TestEnvHolder {

  @Autowired
  private NicoUnsafeIdmYoutubeDlDownloader downloader;

  @Test
  void testDownload() throws InterruptedException {
    var downloadStatus = downloader.download(
        "https://www.nicovideo.jp/watch/sm37987111",
        Path.of("D:/11134/Videos/Vocaloid Coding POC"),
        "ニジイロストーリーズ（VOCALOID ver.）.mp4");
    assertTrue(downloadStatus.isSucceed());
  }
}