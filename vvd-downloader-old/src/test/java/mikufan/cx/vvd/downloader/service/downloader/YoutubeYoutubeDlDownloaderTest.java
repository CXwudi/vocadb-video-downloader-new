package mikufan.cx.vvd.downloader.service.downloader;

import mikufan.cx.vvd.common.naming.FileNameUtil;
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
    "config.downloader.youtube.youtube-dl-path=D:/11134/Videos/Vocaloid Coding POC/youtube-dl-niconico-enhanced.exe",
    "config.downloader.youtube.ffmpeg-path=D:/11134/Videos/Vocaloid Coding POC/ffmpeg.exe",
    "config.downloader.youtube.youtube-dl-options.--cookies=D:/11134/Videos/Vocaloid Coding POC/youtube cookies.txt"
})
@Disabled
class YoutubeYoutubeDlDownloaderTest extends TestEnvHolder {

  @Autowired
  private YoutubeYoutubeDlDownloader downloader;

  @Test
  void testDownload() throws InterruptedException {
    var downloadStatus = downloader.downloadPvAndThumbnail(
        "https://www.youtube.com/watch?v=TD0Os2aH2rU",
        Path.of("D:/11134/Videos/Vocaloid Coding POC"),
        FileNameUtil.removeIllegalChars("To be continued... / 初音ミク - 青屋夏生 - video.mkv"),
        FileNameUtil.removeIllegalChars("To be continued... / 初音ミク - 青屋夏生 - pic.webp"));
    assertTrue(downloadStatus.isSucceed());
  }
}