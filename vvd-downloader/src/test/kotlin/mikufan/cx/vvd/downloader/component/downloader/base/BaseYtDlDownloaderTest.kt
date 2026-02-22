package mikufan.cx.vvd.downloader.component.downloader.base

import tools.jackson.databind.ObjectMapper
import mikufan.cx.vvd.commonkt.vocadb.api.model.SongForApiContract
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.downloader.component.downloader.implementation.BilibiliYtDlDownloader
import mikufan.cx.vvd.downloader.component.downloader.implementation.NicoNicoYtDlDownloader
import mikufan.cx.vvd.downloader.component.downloader.implementation.SoundCloudYtDlDownloader
import mikufan.cx.vvd.downloader.component.downloader.implementation.YoutubeYtDlDownloader
import mikufan.cx.vvd.downloader.config.IOConfig
import mikufan.cx.vvd.downloader.model.Parameters
import mikufan.cx.vvd.downloader.model.VSongTask
import mikufan.cx.vvd.downloader.util.SpringBootTestWithTestProfile
import mikufan.cx.vvd.downloader.util.loadResourceAsString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@SpringBootTestWithTestProfile(
  customProperties = [
    "config.downloader.NicoNicoDouga.youtube-dl.launch-cmd=yt-dlp",
    "config.downloader.Youtube.youtube-dl.launch-cmd=yt-dlp",
    "config.downloader.Bilibili.youtube-dl.launch-cmd=yt-dlp",
    "config.downloader.sound-cloud.youtube-dl.launch-cmd=yt-dlp",
  ]
)
@Disabled("do not download PVs for every commit in CI")
class BaseYtDlDownloaderTest(
  private val nicoNicoYtDlDownloader: NicoNicoYtDlDownloader,
  private val youtubeYtDlDownloader: YoutubeYtDlDownloader,
  private val bilibiliYtDlDownloader: BilibiliYtDlDownloader,
  private val soundCloudYtDlDownloader: SoundCloudYtDlDownloader,
  private val objectMapper: ObjectMapper,
  ioConfig: IOConfig,
) {
  private val outputDir = ioConfig.outputDirectory

  private fun testDownload(resourceName: String, pvNum: Int, downloader: BaseYtDlDownloader) {
    val song = objectMapper.readValue(
      loadResourceAsString(resourceName),
      SongForApiContract::class.java
    )
    val nicoPv = song.pvs[pvNum]
    val task = VSongTask(VSongLabel.builder().build(), Parameters(song))
    val result = downloader.download(nicoPv, task, outputDir)
    assertThat(result.isSuccess).isTrue()
  }

  @Test
  fun downloadVideoAndThumbnailFromNiconico() {
    testDownload(
      "2021年V家新曲-label/【初音ミク】vividn hop【DoubleLift】[358515]-songInfo.json",
      0,
      nicoNicoYtDlDownloader
    )
  }

  @Test
  fun downloadVideoAndThumbnailFromYoutube() {
    testDownload(
      "2021年V家新曲-label/【初音ミク】My Stage With You【cannibaltim, 周小蚕】[373813]-songInfo.json",
      0,
      youtubeYtDlDownloader
    )
  }

  @Test
  fun downloadVideoAndThumbnailFromBilibili() {
    testDownload(
      "2021年V家新曲-label/【初音ミク, 神威がくぽ】侵蝕性⇆恋愛症候群【天钦, 动点P】[335778]-songInfo.json",
      0,
      bilibiliYtDlDownloader
    )
  }

  @Test
  fun downloadVideoAndThumbnailFromSoundcloud() {
    testDownload(
      "2021年V家新曲-label/【初音ミク】WANCO!!【Twinfield】[336290]-songInfo.json",
      2,
      soundCloudYtDlDownloader
    )
  }
}
