package mikufan.cx.vvd.downloader.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.vocadb.model.SongForApi;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * @author CX无敌
 * @date 2020-12-19
 */
@Service @Slf4j @Validated
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MainService implements Runnable{

  IOService ioService;

  PvDecider pvDecider;

  DownloaderDecider downloaderDecider;

  DownloadService downloadService;

  /**
   * main entry method
   */
  @Override
  public void run() {
    var allSongsToBeDownloadedInOrder = ioService.getAllSongsToBeDownloadedInOrder();
    allSongsToBeDownloadedInOrder.forEach(this::handleDownload);

  }

  private void handleDownload(SongForApi toBeDownload) {
    // need an annotation to validate that this song is downloadable
    var pvs = toBeDownload.getPvs();
    //0. choose the preference pv
    var chosenPv = pvDecider.choosePreferredPv(pvs);
    //1. choose the downloader
    var suitableDownloader = downloaderDecider.getSuitableDownloader(chosenPv.getService());
    //2. download song and thumbnail
    var downloadStatus = downloadService.handleDownload(suitableDownloader, chosenPv);
    //3. move input json to output dir

  }
}
