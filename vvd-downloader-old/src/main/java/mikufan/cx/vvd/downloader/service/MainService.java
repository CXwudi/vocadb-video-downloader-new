package mikufan.cx.vvd.downloader.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.naming.FileNameUtil;
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
    // what is returned is what is used for tracking input files to be downloaded
    var allSongsToBeDownloadedInOrder = ioService.getAllSongsToBeDownloadedInOrder();
    allSongsToBeDownloadedInOrder.forEach(this::handleDownload);
    log.info("All done, thanks for using vocadb-video-downloader - vvd-downloader submodule");
  }

  private void handleDownload(SongForApi toBeDownload) {
    // need an annotation to validate that this song is downloadable
    log.info("Start handling download pv and thumbnail for {}", FileNameUtil.buildBasicFileNameForSong(toBeDownload));
    //0. choose the preference pv
    //TODO: create a new POJO called DownloadContext,
    // and refactor all following methods to only take a single DownloadInfo parameter,
    // create a DownloadInfoBuilder instance here to store all return values from methods
    var chosenPv = pvDecider.choosePreferredPv(toBeDownload.getPvs());
    //1. choose the downloader
    var downloaderInfo = downloaderDecider.getSuitableDownloaderAndInfo(chosenPv.getService());
    //2. download song and thumbnail
    var downloadStatus = downloadService.handleDownload(downloaderInfo, chosenPv, toBeDownload);
    //3. move input json to output dir

    /* write vsong resource json, and vvd-extractor use vsong resource json as input file progress trackers
     the reason is that, we should not write code once again about extension guessing,
     and extractor chosen is determined by pv service and file extension format from the downloader
     and audio tagger need the chosen pv info to write custom tags
    */
    ioService.recordDownloadedSong(downloadStatus, downloaderInfo, toBeDownload, chosenPv);
  }
}
