package mikufan.cx.vvd.downloader.config.downloader

import mikufan.cx.vvd.commonkt.vocadb.PVServicesEnum

abstract class SoundCloudBaseDownloaderCondition : DownloaderBaseCondition {
  override val pvServices = PVServicesEnum.SOUNDCLOUD
}

class SoundCloudYtDlCondition : SoundCloudBaseDownloaderCondition() {
  override val downloaderName: String = SC_YTDL
}