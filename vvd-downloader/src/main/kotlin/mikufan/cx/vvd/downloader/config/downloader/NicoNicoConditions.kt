package mikufan.cx.vvd.downloader.config.downloader

import mikufan.cx.vvd.downloader.util.PVServicesEnum

/**
 * conditions for niconico downloader configs
 * @date 2021-07-18
 * @author CX无敌
 */

sealed class NicoNicoDownloaderBaseCondition : DownloaderBaseCondition {
  override val pvServices = PVServicesEnum.NICONICODOUGA
}

class NicoNicoYtDlCondition : NicoNicoDownloaderBaseCondition() {
  override val downloaderName: String = NND_YOUTUBE_DL
}
