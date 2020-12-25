package mikufan.cx.vvd.downloader.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.vocadb.model.PV;
import mikufan.cx.vvd.downloader.config.IOConfig;
import mikufan.cx.vvd.downloader.config.MechanismConfig;
import mikufan.cx.vvd.downloader.service.downloader.DownloadStatus;
import mikufan.cx.vvd.downloader.service.downloader.PvDownloader;
import org.springframework.stereotype.Service;

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

  @Override
  public DownloadStatus handleDownload(PvDownloader realDownloader, PV pv) {
    log.info("Downloading {} using {}", pv.getUrl(), realDownloader.getName());
    return null;
  }
}
