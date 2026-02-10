package mikufan.cx.vvd.downloader.config.downloader

import mikufan.cx.vvd.commonkt.vocadb.api.model.PVService

/**
 * conditions for youtube downloader configs
 * @date 2022-05-21
 * @author CX无敌
 */

sealed class YoutubeDownloaderBaseCondition : DownloaderBaseCondition {
  override val pvServices = PVService.YOUTUBE
}

class YoutubeYtDlCondition : YoutubeDownloaderBaseCondition() {
  override val downloaderName: String = YT_YTDL
}
