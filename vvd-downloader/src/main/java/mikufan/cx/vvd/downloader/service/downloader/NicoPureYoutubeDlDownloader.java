package mikufan.cx.vvd.downloader.service.downloader;

import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.downloader.config.downloader.NicoPureYoutubeDlConfig;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author CX无敌
 * @date 2020-12-21
 */
@Service @Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class NicoPureYoutubeDlDownloader implements PvDownloader {

  NicoPureYoutubeDlConfig config;

  @Override
  public String getName() {
    return "Niconico Video Downloader by youtube-dl only";
  }

  @Override
  public DownloadStatus download(String url, Path dir, String fileName) throws InterruptedException {
    var youtubeDlRequest = new YoutubeDLRequest(
        url,
        dir.toAbsolutePath().toString(),
        config.getYoutubeDlPath().toAbsolutePath().toString());
    youtubeDlRequest
        .setOptions(config.getYoutubeDlOptions())
        .setOption("-o", fileName);

    YoutubeDLResponse youtubeDlResponse;

    try {
      youtubeDlResponse = YoutubeDL.execute(youtubeDlRequest, log::info, log::debug);
    } catch (YoutubeDLException e) {
      if (e.getCause() instanceof InterruptedException){
        throw (InterruptedException) e.getCause();
      } else {
        log.error("YoutubeDLException in download method", e);
        return DownloadStatus.failure(e.getMessage());
      }
    }

    if (youtubeDlResponse.isSuccess() && Files.exists(dir.resolve(fileName))){
      return DownloadStatus.success();
    } else {
      return DownloadStatus.failure(
          String.format("Can not find the downloaded file or download fails, see error message below%n%s",
              youtubeDlResponse.getErr()));
    }
  }
}
