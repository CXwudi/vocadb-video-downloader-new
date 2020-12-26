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
import mikufan.cx.vvd.downloader.service.downloader.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

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
    var fileName = FileNameUtil.buildPvFileName(song, getExtension(realDownloader));
    log.info("Start downloading to {} using {}", fileName, realDownloader.getName());

    var maxAllowedRetryCount = mechanismConfig.getMaxAllowedRetryCount();
    DownloadStatus currentStatus = null;
    // max + 1 is the total attemp, including the first attempt, retry
    var statusList = new ArrayList<DownloadStatus>(maxAllowedRetryCount + 1);

    for (int i = 0; i < maxAllowedRetryCount + 1 && notSuccess(currentStatus); i++){
      log.debug("Starting downloading attempt #{}", i);
      currentStatus = realDownloader.download(pv.getUrl(), ioConfig.getOutputDirectory(), fileName);

      if (currentStatus.isSucceed()){
        log.info("Downloading success, file is {}", ioConfig.getOutputDirectory().resolve(fileName));
        return currentStatus;
      } else {
        statusList.add(currentStatus);
        log.warn("Downloading failed on attempt #{}, error message = {}", i, currentStatus.getDescription());
      }
    }

    return DownloadStatus.failure(statusList.stream()
        .map(DownloadStatus::getDescription)
        .collect(Collectors.joining(", ", "All failure messages = [", "]")));
  }

  private String getExtension(PvDownloader realDownloader) {
    if (realDownloader instanceof NicoPureYoutubeDlDownloader){
      return ".mp4";
    } else if (realDownloader instanceof NicoUnsafeIdmYoutubeDlDownloader){
      return ".mp4";
    } else if (realDownloader instanceof YoutubeYoutubeDlDownloader){
      return ".mkv";
    } else if (realDownloader instanceof BilibiliYoutubeDlDownloader){
      return ".mp4";
    } else {
      throw new RuntimeVocaloidException(
          String.format("Un-supported pv downloader, %s", realDownloader.getName()));
    }
  }

  private boolean notSuccess(DownloadStatus currentStatus) {
    return Objects.isNull(currentStatus) || !currentStatus.isSucceed();
  }
}
