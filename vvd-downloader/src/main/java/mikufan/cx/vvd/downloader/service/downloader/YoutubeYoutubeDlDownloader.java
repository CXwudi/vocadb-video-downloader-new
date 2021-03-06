package mikufan.cx.vvd.downloader.service.downloader;

import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.downloader.config.downloader.YoutubeYoutubeDlConfig;
import mikufan.cx.vvd.downloader.label.DownloadStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @author CX无敌
 * @date 2020-12-21
 */
@Service @Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class YoutubeYoutubeDlDownloader implements PvDownloader {

  YoutubeYoutubeDlConfig config;

  @Override
  public String getName() {
    return "Youtube downloader by youtube-dl";
  }

  @Override
  public DownloadStatus downloadPvAndThumbnail(String url, Path dir, String pvFileName, String thumbnailFileName) throws InterruptedException {
    var youtubeDlRequest = new YoutubeDLRequest(
        url,
        dir.toAbsolutePath().toString(),
        config.getYoutubeDlPath().toAbsolutePath().toString());
    var basePvFileName = pvFileName.substring(0, pvFileName.lastIndexOf('.'));
    youtubeDlRequest
        .setOptions(config.getYoutubeDlOptions())
        .setOption("--ffmpeg-location", config.getFfmpegPath().toAbsolutePath().toString())
        // use the file name without extension,
        // so that youtube-dl or ffmpeg won't add his own extension after our extension
        .setOption("-o", basePvFileName)
        .setOption("--write-thumbnail");

    YoutubeDLResponse youtubeDlResponse;
    try {
      youtubeDlResponse = YoutubeDL.execute(youtubeDlRequest, log::info, log::debug);
    } catch (YoutubeDLException e) {
      if (e.getCause() instanceof InterruptedException){
        throw (InterruptedException) e.getCause();
      } else {
        log.error("YoutubeDLException in downloadPvAndThumbnail method", e);
        return DownloadStatus.failure(e.getMessage());
      }
    }

    // we can't control how thumbnail filename looks like, but change it after the download
    var expectedThumbnailFileName = pvFileName.replace(
        // replace video extension to thumbnail extension to get the expected thumbnail file name
        pvFileName.substring(pvFileName.lastIndexOf('.')),
        thumbnailFileName.substring(thumbnailFileName.lastIndexOf('.'))
    );

    // a hack to rename .jpg to .webp TODO: fix it with refactor
    var unexpectedThumbnailFileName = pvFileName.replace(
        // replace video extension to thumbnail extension to get the expected thumbnail file name
        pvFileName.substring(pvFileName.lastIndexOf('.')),
        ".jpg"
    );
    if (Files.exists(dir.resolve(unexpectedThumbnailFileName))){
      try {
        log.debug("Renaming unexpected .jpg thumbnail file from {} to {}", unexpectedThumbnailFileName, expectedThumbnailFileName);
        Files.move(dir.resolve(unexpectedThumbnailFileName), dir.resolve(expectedThumbnailFileName), StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        return DownloadStatus.failure(String.format("Fail to rename unexpected .jpg thumbnail file to %s", expectedThumbnailFileName));
      }
    }

    try {
      log.debug("Renaming thumbnail file from {} to {}", expectedThumbnailFileName, thumbnailFileName);
      Files.move(dir.resolve(expectedThumbnailFileName), dir.resolve(thumbnailFileName), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      return DownloadStatus.failure(String.format("Fail to rename thumbnail file to %s", thumbnailFileName));
    }

    // a hack to rename .webm to .mkv TODO: fix it with refactor
    if (Files.exists(dir.resolve(basePvFileName + ".webm"))){
      try {
        log.debug("Renaming unexpected .webm video file from {} to {}", basePvFileName + ".webm", pvFileName);
        Files.move(dir.resolve(basePvFileName + ".webm"), dir.resolve(pvFileName), StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        return DownloadStatus.failure(String.format("Fail to rename unexpected .webm video file to %s", pvFileName));
      }
    }

    if (youtubeDlResponse.isSuccess() &&
        Files.exists(dir.resolve(pvFileName)) &&
        Files.exists(dir.resolve(thumbnailFileName))){
      return DownloadStatus.success();
    } else {
      return DownloadStatus.failure(
          String.format("Can not find the downloaded file or download fails, see error message below%n%s",
              youtubeDlResponse.getErr()));
    }
  }
}
