package mikufan.cx.vvd.downloader.component.downloader

import com.fasterxml.jackson.databind.ObjectMapper
import mikufan.cx.vvd.downloader.config.DownloadConfig
import mikufan.cx.vvd.downloader.config.EnvironmentConfig
import mikufan.cx.vvd.downloader.util.PVServicesEnum
import mikufan.cx.vvd.downloader.util.SpringBootDirtyTestWithTestProfile
import mikufan.cx.vvd.downloader.util.SpringShouldSpec
import org.apache.tika.Tika
import org.springframework.stereotype.Component
import java.nio.file.Path

@SpringBootDirtyTestWithTestProfile(
  customProperties = [
    "config.environment.mediainfo-launch-cmd=D:\\coding-workspace\\Vocaloid Coding POC\\MediaInfo.exe"
  ]
)
class BaseCliDownloaderTest : SpringShouldSpec({
  // TODO: add test
})

@Component
class DummySuccessCliDownloader(
  downloadConfig: DownloadConfig,
  tika: Tika,
  environmentConfig: EnvironmentConfig,
  objectMapper: ObjectMapper,
) : BaseCliDownloader(
  downloadConfig,
  tika,
  environmentConfig,
  objectMapper
) {
  override fun buildCommands(url: String, baseFileName: String, outputDirectory: Path): List<String> {
    // TODO: need to add some mp4, mkv, webm, ts video file
    // and webp, jpg thumbnail files
    // probably some mp3, aac, m4a files
    return listOf("java", "--version")
  }

  override val downloaderName: String = "dummy-success-dl"
  override val targetPvService: PVServicesEnum = PVServicesEnum.NICONICODOUGA
}
