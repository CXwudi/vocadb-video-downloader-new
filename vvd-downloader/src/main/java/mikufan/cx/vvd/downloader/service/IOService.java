package mikufan.cx.vvd.downloader.service;

import mikufan.cx.vvd.common.vocadb.model.PV;
import mikufan.cx.vvd.common.vocadb.model.SongForApi;
import mikufan.cx.vvd.downloader.label.DownloadStatus;
import mikufan.cx.vvd.downloader.label.DownloaderInfo;

import java.util.List;

/**
 * @author CX无敌
 * @date 2020-12-20
 */
public interface IOService {
  List<SongForApi> getAllSongsToBeDownloadedInOrder();

  /**
   * record the following to a {@link mikufan.cx.vvd.common.label.VSongResource} json file
   * @param downloadStatus how is the downloading, if failed, is used for writing failed object json
   * @param downloaderInfo the info about this downloading
   * @param song the song is been downloaded, used for setting file name
   * @param chosenPv the pv that is chosen to be downloaded, used for setting
   */
  void recordDownloadedSong(DownloadStatus downloadStatus, DownloaderInfo downloaderInfo, SongForApi song, PV chosenPv);
}
