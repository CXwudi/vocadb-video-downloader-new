package mikufan.cx.vvd.downloader.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import mikufan.cx.vvd.common.vocadb.model.SongForApi;
import org.springframework.stereotype.Service;

/**
 * @author CX无敌
 * @date 2020-12-19
 */
@Service @Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MainService implements Runnable{

  IOService ioService;

  PvDecider pvDecider;

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

    //1. choose the downloader

    //2. download song and thumbnial

    //3. move input json to output dir

    //4. write resource json
  }
}
