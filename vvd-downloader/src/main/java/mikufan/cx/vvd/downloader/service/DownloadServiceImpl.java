package mikufan.cx.vvd.downloader.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException;
import mikufan.cx.vvd.common.util.FileNameUtil;
import mikufan.cx.vvd.common.vocadb.model.PV;
import mikufan.cx.vvd.common.vocadb.model.SongForApi;
import mikufan.cx.vvd.downloader.config.IOConfig;
import mikufan.cx.vvd.downloader.config.MechanismConfig;
import mikufan.cx.vvd.downloader.label.DownloadStatus;
import mikufan.cx.vvd.downloader.service.downloader.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author CX无敌
 * @date 2020-12-25
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DownloadServiceImpl implements DownloadService {

  IOConfig ioConfig;

  MechanismConfig mechanismConfig;

  @Override @SneakyThrows(InterruptedException.class)
  public DownloadStatus handleDownload(PvDownloader realDownloader, PV pv, SongForApi song) {
    var pvFileName = FileNameUtil.buildPvFileName(song, getPvExtension(realDownloader));
    var thumbnailFileName = FileNameUtil.buildThumbnailFileName(song, getThumbnailExtension(realDownloader));
    log.info("Start downloading pv to {}, thumbnail to {} using {}", pvFileName, thumbnailFileName, realDownloader.getName());

    var maxAllowedRetryCount = mechanismConfig.getMaxAllowedRetryCount();
    DownloadStatus currentStatus = null;
    // max + 1 is the total attemp, including the first attempt, retry
    var statusList = new ArrayList<DownloadStatus>(maxAllowedRetryCount + 1);

    for (int i = 0; i < maxAllowedRetryCount + 1 && notSuccess(currentStatus); i++){
      log.debug("Starting downloading attempt #{}", i);
      currentStatus = realDownloader.downloadPvAndThumbnail(pv.getUrl(), ioConfig.getOutputDirectory(), pvFileName, thumbnailFileName);

      if (currentStatus.isSucceed()){
        log.info("Downloading success, pv file is {}, thumbnail file is {}",
            ioConfig.getOutputDirectory().resolve(pvFileName), ioConfig.getOutputDirectory().resolve(thumbnailFileName));
        return currentStatus;
      } else {
        statusList.add(currentStatus);
        log.warn("Downloading failed on attempt #{}, error message = {}", i, currentStatus.getDescription());
      }
    }

    return DownloadStatus.merge(statusList.toArray(DownloadStatus[]::new));
  }

  private String getPvExtension(PvDownloader realDownloader) {
    if (realDownloader instanceof NicoPureYoutubeDlDownloader){
      return ".mp4";
    } else if (realDownloader instanceof NicoUnsafeIdmYoutubeDlDownloader){
      return ".mp4";
    } else if (realDownloader instanceof YoutubeYoutubeDlDownloader){
      return ".mkv";
    } else if (realDownloader instanceof BilibiliYoutubeDlDownloader){
      return ".flv";
    } else {
      throw new RuntimeVocaloidException(
          String.format("Un-supported pv downloader, %s", realDownloader.getName()));
    }
  }

  private String getThumbnailExtension(PvDownloader realDownloader) {
    if (realDownloader instanceof NicoPureYoutubeDlDownloader){
      return ".jpg";
    } else if (realDownloader instanceof NicoUnsafeIdmYoutubeDlDownloader){
      return ".jpg";
    } else if (realDownloader instanceof YoutubeYoutubeDlDownloader){
      return ".webp";
    } else if (realDownloader instanceof BilibiliYoutubeDlDownloader){
      return ".jpg";
    } else {
      throw new RuntimeVocaloidException(
          String.format("Un-supported pv downloader, %s", realDownloader.getName()));
    }
  }

  private boolean notSuccess(DownloadStatus currentStatus) {
    return Objects.isNull(currentStatus) || !currentStatus.isSucceed();
  }
}
