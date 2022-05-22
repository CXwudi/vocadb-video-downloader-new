package mikufan.cx.vvd.downloader.component.downloader

import com.fasterxml.jackson.databind.ObjectMapper
import mikufan.cx.vvd.downloader.component.downloader.base.BaseYtDlDownloader
import mikufan.cx.vvd.downloader.config.DownloadConfig
import mikufan.cx.vvd.downloader.config.EnvironmentConfig
import mikufan.cx.vvd.downloader.config.downloader.BILI_YTDL
import mikufan.cx.vvd.downloader.config.downloader.BilibiliYtDlConfigs
import mikufan.cx.vvd.downloader.config.downloader.NicoNicoYtDlCondition
import mikufan.cx.vvd.downloader.util.PVServicesEnum
import org.apache.tika.Tika
import org.springframework.context.annotation.Conditional
import org.springframework.stereotype.Component

/**
 * @date 2022-05-21
 * @author CX无敌
 */
@Component
@Conditional(NicoNicoYtDlCondition::class)
class BilibiliYtDlDownloader(
  config: BilibiliYtDlConfigs,
  downloadConfig: DownloadConfig,
  tika: Tika,
  environmentConfig: EnvironmentConfig,
  objectMapper: ObjectMapper,
) : BaseYtDlDownloader(
  downloadConfig, tika, environmentConfig, objectMapper
) {
  override val downloaderName: String = BILI_YTDL
  override val targetPvService: PVServicesEnum = PVServicesEnum.BILIBILI
  override val launchCmd: List<String> = config.launchCmd
  override val externalArgs: List<String> = config.externalArgs
}