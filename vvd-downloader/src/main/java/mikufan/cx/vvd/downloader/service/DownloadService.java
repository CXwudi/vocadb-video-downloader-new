package mikufan.cx.vvd.downloader.service;

import mikufan.cx.vvd.common.vocadb.model.SongForApi;
import mikufan.cx.vvd.downloader.service.downloader.DownloadStatus;
import mikufan.cx.vvd.downloader.service.downloader.PvDownloader;

/**
 * @author CX无敌
 * @date 2020-12-21
 */
public interface DownloadService {

  DownloadStatus handleDownload(PvDownloader realDownloader, SongForApi song);
}
