package mikufan.cx.vvd.downloader.label;

import lombok.Builder;
import lombok.Value;
import mikufan.cx.vvd.downloader.service.downloader.PvDownloader;

/**
 * @author CX无敌
 * @date 2020-12-30
 */
@Value @Builder
public class DownloaderInfo {
  PvDownloader pvDownloader;
  String pvFileExtension;
  String thumbnailFileExtension;
  String audioFileExtension;
}
