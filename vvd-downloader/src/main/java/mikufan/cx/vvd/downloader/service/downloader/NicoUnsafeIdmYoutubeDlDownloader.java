package mikufan.cx.vvd.downloader.service.downloader;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

/**
 * @author CX无敌
 * @date 2020-12-21
 */
@Service @Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class NicoUnsafeIdmYoutubeDlDownloader implements PvDownloader {



  @Override
  public String getName() {
    return "Niconico Video Downloader by IDM and youtube-dl (unsafe)";
  }

  @Override
  public DownloadStatus download(String url, Path dir, String fileName) throws InterruptedException {
    return null;
  }
}
