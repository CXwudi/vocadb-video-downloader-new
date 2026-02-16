package mikufan.cx.vvd.downloader.config.downloader

import mikufan.cx.vvd.commonkt.vocadb.api.model.PVService

/**
 * conditions for bilibili downloader configs
 * @date 2022-05-21
 * @author CX无敌
 */

sealed class BilibiliDownloaderBaseCondition : DownloaderBaseCondition {
  override val pvServices = PVService.BILIBILI
}

class BilibiliYtDlCondition : BilibiliDownloaderBaseCondition() {
  override val downloaderName: String = BILI_YTDL
}
