package mikufan.cx.vvd.downloader.component.downloader.base

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.assertions.fail
import io.kotest.matchers.string.shouldContain
import mikufan.cx.vvd.commonkt.vocadb.PVServicesEnum
import mikufan.cx.vvd.downloader.config.DownloadConfig
import mikufan.cx.vvd.downloader.config.EnvironmentConfig
import mikufan.cx.vvd.downloader.config.IOConfig
import mikufan.cx.vvd.downloader.util.SpringBootDirtyTestWithTestProfile
import mikufan.cx.vvd.downloader.util.SpringShouldSpec
import org.apache.tika.Tika
import org.hibernate.validator.internal.util.Contracts.assertTrue
import java.nio.file.Path
import kotlin.io.path.*

/**
 * This test requires the docker env
 */
@SpringBootDirtyTestWithTestProfile
class BaseCliDownloaderTest(
  ioConfig: IOConfig,
  private val downloadConfig: DownloadConfig,
  private val tika: Tika,
  private val environmentConfig: EnvironmentConfig,
  private val objectMapper: ObjectMapper,
) : SpringShouldSpec({

  val outputDir = ioConfig.outputDirectory

  val copyTestSource = fun(originFileNameWithExtension: String, targetFileNameWithoutExtension: String) {
    val source = Path("../test-files/$originFileNameWithExtension")
    val extension = source.extension
    val targetPath = source.copyTo(outputDir / "$targetFileNameWithoutExtension.$extension")
    targetPath.toFile().deleteOnExit() // this is having problem
  }

  val mockDownloader = DummyCliDownloader(downloadConfig, tika, environmentConfig, objectMapper)

  context("assume download success") {

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

    for (number in 1..4) {
      should("recognized downloaded files for song$number") {
        runCatching {
          mockDownloader.tryDownload("fake url", "【vocalist】song$number【producer】[39393]", outputDir)
        }.onFailure {
          fail("should not fail for song$number")
        }.onSuccess { (pvFile, audioFile, thumbnailFile) ->
          if (number == 2) {
            assertTrue(pvFile?.exists() == true && audioFile?.exists() == true, "pvFile and audioFile should exist")
          } else {
            assertTrue(pvFile?.exists() == true || audioFile?.exists() == true, "pvFile or audioFile should exist")
          }
          assertTrue(thumbnailFile.exists(), "thumbnailFile should exist")
        }
      }
    }
  }

  context("assume failed download") {

    should("throw exception about not finding the file") {
      runCatching {
        mockDownloader.tryDownload("fake url", "【vocalist】song doesn't exist【producer】[39393]", outputDir)
      }.onSuccess {
        fail("should not success")
      }.onFailure { e ->
        e.message shouldContain "None of"
        e is IllegalStateException
      }
    }
  }
})

/**
 * can't use mockk because of https://github.com/mockk/mockk/issues/321
 */
class DummyCliDownloader(
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
  override fun buildCommand(url: String, baseFileName: String, outputDirectory: Path): List<String> =
    listOf("java", "--version")

  override val downloaderName: String = "dummy-success-dl"
  override val targetPvService: PVServicesEnum = PVServicesEnum.NICONICODOUGA
}
