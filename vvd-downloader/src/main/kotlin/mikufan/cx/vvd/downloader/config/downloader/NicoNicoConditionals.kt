package mikufan.cx.vvd.downloader.config.downloader

import mikufan.cx.vvd.downloader.util.PVServicesEnum

/**
 * @date 2021-07-18
 * @author CX无敌
 */

sealed class NicoNicoDownloaderBaseConditional : DownloaderBaseConditional {
  override val pvServices = PVServicesEnum.NICONICODOUGA
}

class NicoNicoYtDlConditional : NicoNicoDownloaderBaseConditional() {
  override val downloaderName: String = NND_YOUTUBE_DL
}