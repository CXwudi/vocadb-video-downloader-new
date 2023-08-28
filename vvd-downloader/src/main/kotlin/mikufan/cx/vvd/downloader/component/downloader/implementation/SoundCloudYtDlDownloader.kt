package mikufan.cx.vvd.downloader.component.downloader.implementation

import com.fasterxml.jackson.databind.ObjectMapper
import mikufan.cx.vvd.commonkt.vocadb.PVServicesEnum
import mikufan.cx.vvd.downloader.component.downloader.base.BaseYtDlDownloader
import mikufan.cx.vvd.downloader.config.DownloadConfig
import mikufan.cx.vvd.downloader.config.EnvironmentConfig
import mikufan.cx.vvd.downloader.config.downloader.SC_YTDL
import mikufan.cx.vvd.downloader.config.downloader.SoundCloudYtDlCondition
import mikufan.cx.vvd.downloader.config.downloader.SoundCloudYtDlConfig
import org.apache.tika.Tika
import org.springframework.context.annotation.Conditional
import org.springframework.stereotype.Component

@Component
@Conditional(SoundCloudYtDlCondition::class)
class SoundCloudYtDlDownloader(
  config: SoundCloudYtDlConfig,
  downloadConfig: DownloadConfig,
  tika: Tika,
  environmentConfig: EnvironmentConfig,
  objectMapper: ObjectMapper,
) : BaseYtDlDownloader(
  downloadConfig, tika, environmentConfig, objectMapper
) {
  override val downloaderName: String = "$SC_YTDL or its forks"
  override val targetPvService: PVServicesEnum = PVServicesEnum.SOUNDCLOUD
  override val launchCmd: List<String> = config.launchCmd
  override val externalArgs: List<String> = config.externalArgs
}