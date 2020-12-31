package mikufan.cx.vvd.downloader.service;

import mikufan.cx.vvd.downloader.label.DownloaderInfo;

/**
 * @author CX无敌
 * @date 2020-12-20
 */
public interface DownloaderDecider {

  DownloaderInfo getSuitableDownloaderAndInfo(String pvService);
}
