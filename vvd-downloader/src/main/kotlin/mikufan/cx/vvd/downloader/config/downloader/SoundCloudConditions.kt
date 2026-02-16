package mikufan.cx.vvd.downloader.config.downloader

import mikufan.cx.vvd.commonkt.vocadb.api.model.PVService

abstract class SoundCloudBaseDownloaderCondition : DownloaderBaseCondition {
  override val pvServices = PVService.SOUNDCLOUD
}

class SoundCloudYtDlCondition : SoundCloudBaseDownloaderCondition() {
  override val downloaderName: String = SC_YTDL
}
