package mikufan.cx.vvd.downloader.service;

import mikufan.cx.vvd.common.vocadb.model.SongForApi;
import mikufan.cx.vvd.downloader.service.downloader.DownloadStatus;

import java.util.List;

/**
 * @author CX无敌
 * @date 2020-12-20
 */
public interface IOService {
  List<SongForApi> getAllSongsToBeDownloadedInOrder();
  void recordDownloadedSong(DownloadStatus downloadStatus, SongForApi song);
}
