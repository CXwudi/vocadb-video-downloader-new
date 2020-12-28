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
 * @date 2020-12-26
 */
@SpringBootTest(properties = {
    "config.downloader.nico-pure-youtube-dl.youtube-dl-path=D:/11134/Videos/Vocaloid Coding POC/youtube-dl-niconico-enhanced.exe",
    "config.downloader.nico-pure-youtube-dl.youtube-dl-options.--cookies=D:/11134/Videos/Vocaloid Coding POC/niconico cookies.txt"
})
@Disabled
class NicoPureYoutubeDlDownloaderTest extends TestEnvHolder {

  @Autowired
  private NicoPureYoutubeDlDownloader downloader;

  @Test
  void testDownload() throws InterruptedException {
    var downloadStatus = downloader.downloadPvAndThumbnail(
        "https://www.nicovideo.jp/watch/sm37879497",
        Path.of("D:/11134/Videos/Vocaloid Coding POC"),
        "【初音ミクNT】 ふたつカゲボウシ【Notzan ACT】-video.mp4",
        "【初音ミクNT】 ふたつカゲボウシ【Notzan ACT】-pic.jpg");
    assertTrue(downloadStatus.isSucceed());
  }
}