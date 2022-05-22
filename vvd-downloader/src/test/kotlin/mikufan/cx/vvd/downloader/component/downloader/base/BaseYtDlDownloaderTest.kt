package mikufan.cx.vvd.downloader.component.downloader.base

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import mikufan.cx.vocadbapiclient.model.SongForApiContract
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.downloader.component.downloader.BilibiliYtDlDownloader
import mikufan.cx.vvd.downloader.component.downloader.NicoNicoYtDlDownloader
import mikufan.cx.vvd.downloader.component.downloader.YoutubeYtDlDownloader
import mikufan.cx.vvd.downloader.config.IOConfig
import mikufan.cx.vvd.downloader.model.Parameters
import mikufan.cx.vvd.downloader.model.VSongTask
import mikufan.cx.vvd.downloader.util.SpringBootDirtyTestWithTestProfile
import mikufan.cx.vvd.downloader.util.SpringShouldSpec
import mikufan.cx.vvd.downloader.util.loadResourceAsString
import org.junit.jupiter.api.Disabled

@SpringBootDirtyTestWithTestProfile(
  customProperties = [
    "config.downloader.NicoNicoDouga.youtube-dl.launch-cmd=yt-dlp",
    "config.downloader.Youtube.youtube-dl.launch-cmd=yt-dlp",
    "config.downloader.Bilibili.youtube-dl.launch-cmd=yt-dlp",
  ]
)
@Disabled("do not download PVs for every commit in CI")
class BaseYtDlDownloaderTest(
  private val nicoNicoYtDlDownloader: NicoNicoYtDlDownloader,
  private val youtubeYtDlDownloader: YoutubeYtDlDownloader,
  private val bilibiliYtDlDownloader: BilibiliYtDlDownloader,
  private val objectMapper: ObjectMapper,
  ioConfig: IOConfig,
) : SpringShouldSpec({
  val outputDir = ioConfig.outputDirectory
  xcontext("downloader powered by youtube-dl and it's variants") {

    val testDownload = fun (resourceName: String, pvNum: Int, downloader: BaseYtDlDownloader) {
      val song = objectMapper.readValue(
        loadResourceAsString(resourceName),
        SongForApiContract::class.java
      )
      val nicoPv = song.pvs?.get(pvNum)!!
      val task = VSongTask(VSongLabel.builder().build(), Parameters(song))
      val result = downloader.download(nicoPv, task, outputDir)
      result.isSuccess shouldBe true
    }

    should("successfully download video and thumbnail from niconico") {
      testDownload("2021年V家新曲-label/【初音ミク】vividn hop【DoubleLift】[358515]-songInfo.json", 0, nicoNicoYtDlDownloader)
    }

    should("successfully download video and thumbnail from youtube") {
      testDownload("2021年V家新曲-label/【初音ミク】My Stage With You【cannibaltim, 周小蚕】[373813]-songInfo.json", 0, youtubeYtDlDownloader)
    }

    should("successfully download video and thumbnail from bilibili") {
      testDownload("2021年V家新曲-label/【初音ミク, 神威がくぽ】侵蝕性⇆恋愛症候群【天钦, 动点P】[335778]-songInfo.json", 0, bilibiliYtDlDownloader)
    }
  }
})
