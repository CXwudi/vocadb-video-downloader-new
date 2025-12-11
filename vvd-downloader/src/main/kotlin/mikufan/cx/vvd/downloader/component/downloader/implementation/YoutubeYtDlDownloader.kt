package mikufan.cx.vvd.downloader.component.downloader.implementation

import tools.jackson.databind.ObjectMapper
import mikufan.cx.vvd.commonkt.vocadb.PVServicesEnum
import mikufan.cx.vvd.downloader.component.downloader.base.BaseYtDlDownloader
import mikufan.cx.vvd.downloader.config.DownloadConfig
import mikufan.cx.vvd.downloader.config.EnvironmentConfig
import mikufan.cx.vvd.downloader.config.downloader.NicoNicoYtDlCondition
import mikufan.cx.vvd.downloader.config.downloader.YT_YTDL
import mikufan.cx.vvd.downloader.config.downloader.YoutubeYtDlConfigs
import org.apache.tika.Tika
import org.springframework.context.annotation.Conditional
import org.springframework.stereotype.Component

/**
 * @date 2022-05-21
 * @author CX无敌
 */
@Component
@Conditional(NicoNicoYtDlCondition::class)
class YoutubeYtDlDownloader(
  config: YoutubeYtDlConfigs,
  downloadConfig: DownloadConfig,
  tika: Tika,
  environmentConfig: EnvironmentConfig,
  objectMapper: ObjectMapper,
) : BaseYtDlDownloader(
  downloadConfig, tika, environmentConfig, objectMapper
) {
  override val downloaderName: String = "$YT_YTDL or its forks"
  override val targetPvService: PVServicesEnum = PVServicesEnum.YOUTUBE
  override val launchCmd: List<String> = config.launchCmd
  override val externalArgs: List<String> = config.externalArgs
}
