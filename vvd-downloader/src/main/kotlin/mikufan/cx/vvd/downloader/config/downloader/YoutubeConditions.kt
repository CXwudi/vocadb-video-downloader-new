package mikufan.cx.vvd.downloader.config.downloader

import mikufan.cx.vvd.downloader.util.PVServicesEnum

/**
 * conditions for youtube downloader configs
 * @date 2022-05-21
 * @author CX无敌
 */

sealed class YoutubeDownloaderBaseCondition : DownloaderBaseCondition {
  override val pvServices = PVServicesEnum.YOUTUBE
}

class YoutubeYtDlCondition : YoutubeDownloaderBaseCondition() {
  override val downloaderName: String = YT_YTDL
}
