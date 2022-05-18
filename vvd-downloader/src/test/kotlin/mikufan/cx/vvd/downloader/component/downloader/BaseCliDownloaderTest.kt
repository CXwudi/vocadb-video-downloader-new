package mikufan.cx.vvd.downloader.component.downloader

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.assertions.fail
import io.mockk.every
import io.mockk.mockk
import mikufan.cx.vocadbapiclient.model.PVContract
import mikufan.cx.vocadbapiclient.model.SongForApiContract
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.downloader.config.DownloadConfig
import mikufan.cx.vvd.downloader.config.EnvironmentConfig
import mikufan.cx.vvd.downloader.config.IOConfig
import mikufan.cx.vvd.downloader.model.Parameters
import mikufan.cx.vvd.downloader.model.VSongTask
import mikufan.cx.vvd.downloader.util.PVServicesEnum
import mikufan.cx.vvd.downloader.util.SpringBootDirtyTestWithTestProfile
import mikufan.cx.vvd.downloader.util.SpringShouldSpec
import org.apache.tika.Tika
import kotlin.io.path.Path
import kotlin.io.path.copyTo
import kotlin.io.path.div
import kotlin.io.path.extension

@SpringBootDirtyTestWithTestProfile
class BaseCliDownloaderTest(
  ioConfig: IOConfig,
  private val downloadConfig: DownloadConfig,
  private val tika: Tika,
  private val environmentConfig: EnvironmentConfig,
  private val objectMapper: ObjectMapper,
) : SpringShouldSpec({

  val outputDir = ioConfig.outputDirectory
  val buildFakeTask = fun(name: String): VSongTask {
    val fakePv = PVContract().apply {
      url = "https://fake.url"
    }
    val fakeSong = SongForApiContract().apply {
      pvs = listOf(fakePv)
      this.defaultName = name
      artistString = "producer feat. vocalist"
      id = 39393
    }
    val fakeTask = VSongTask(VSongLabel.builder().build(), Parameters(fakeSong))
    return fakeTask
  }
  val copyTestSource = fun(originFileNameWithExtension: String, targetFileNameWithoutExtension: String) {
    val source = Path("../test-files/$originFileNameWithExtension")
    val extension = source.extension
    val targetPath = source.copyTo(outputDir / "$targetFileNameWithoutExtension.$extension")
    targetPath.toFile().deleteOnExit()
  }

  context("assume download success") {
    val mockDownloader = mockk<BaseCliDownloader>(relaxed = true)
    every { mockDownloader.buildCommands(any(), any(), any()) }.answers { listOf("java", "--version") }

    copyTestSource("「クリーデンス」／霧島feat.初音ミク [sm39825313].jpg", "【vocalist】song1【producer】[39393]")
    copyTestSource("「クリーデンス」／霧島feat.初音ミク-sm39825313-trim.ts", "【vocalist】song1【producer】[39393]")
    copyTestSource(
      "【世界计划】KAITO&MEIKO「ニジイロストーリーズ」 (Another Vocal版) [459823810_part1].f2-trim.m4a",
      "【vocalist】song2【producer】[39393]"
    )
    copyTestSource(
      "【世界计划】KAITO&MEIKO「ニジイロストーリーズ」 (Another Vocal版) [459823810_part1].f6-trim.mp4",
      "【vocalist】song2【producer】[39393]"
    )
    copyTestSource(
      "【世界计划】KAITO&MEIKO「ニジイロストーリーズ」 (Another Vocal版) [459823810_part1].jpg",
      "【vocalist】song2【producer】[39393]"
    )
    copyTestSource(
      "【初音ミク】LONELY POP feat.初音ミク (Yandere VIP Remix)【オリジナル】 [sm40260101].jpg",
      "【vocalist】song3【producer】[39393]"
    )
    copyTestSource(
      "【初音ミク】LONELY POP feat.初音ミク (Yandere VIP Remix)【オリジナル】 [sm40260101]-trim.mp4",
      "【vocalist】song3【producer】[39393]"
    )
    copyTestSource("Kikuo - 幽体離脱 [UHH2KKN0xoc].webp", "【vocalist】song4【producer】[39393]")
    copyTestSource("Kikuo - 幽体離脱 [UHH2KKN0xoc]-trim.webm", "【vocalist】song4【producer】[39393]")

    (1..4).forEach {
      val fakeTask = buildFakeTask("song$it")
      should("recognized downloaded files for song$it") {
        mockDownloader.download(fakeTask.parameters.songForApiContract!!.pvs!![0], fakeTask, outputDir).onFailure {
          fail("should not fail")
        }
      }
    }
  }
})

abstract class DummyCliDownloader(
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

  override val downloaderName: String = "dummy-success-dl"
  override val targetPvService: PVServicesEnum = PVServicesEnum.NICONICODOUGA
}
