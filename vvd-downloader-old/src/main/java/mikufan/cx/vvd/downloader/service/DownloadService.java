package mikufan.cx.vvd.downloader.service;

import mikufan.cx.vvd.common.vocadb.model.PV;
import mikufan.cx.vvd.common.vocadb.model.SongForApi;
import mikufan.cx.vvd.downloader.label.DownloadStatus;
import mikufan.cx.vvd.downloader.label.DownloaderInfo;

/**
 * @author CX无敌
 * @date 2020-12-21
 */
public interface DownloadService {

  DownloadStatus handleDownload(DownloaderInfo downloaderInfo, PV pv, SongForApi fileName);
}
