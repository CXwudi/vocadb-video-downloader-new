package mikufan.cx.vvd.downloader.service;

import mikufan.cx.vvd.downloader.service.downloader.PvDownloader;

/**
 * @author CX无敌
 * @date 2020-12-20
 */
public interface DownloaderDecider {

  PvDownloader getSuitableDownloader(String pvService);
}
