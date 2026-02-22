package mikufan.cx.vvd.downloader.component.downloader.base

import tools.jackson.databind.ObjectMapper
import mikufan.cx.vvd.commonkt.vocadb.api.model.PVService
import mikufan.cx.vvd.downloader.config.DownloadConfig
import mikufan.cx.vvd.downloader.config.EnvironmentConfig
import mikufan.cx.vvd.downloader.config.IOConfig
import mikufan.cx.vvd.downloader.util.SpringBootTestWithTestProfile
import org.apache.tika.Tika
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.copyTo
import kotlin.io.path.div
import kotlin.io.path.extension
import kotlin.io.path.exists

/**
 * This test requires the docker env
 */
@SpringBootTestWithTestProfile
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BaseCliDownloaderTest(
  ioConfig: IOConfig,
  private val downloadConfig: DownloadConfig,
  private val tika: Tika,
  private val environmentConfig: EnvironmentConfig,
  private val objectMapper: ObjectMapper,
) {

  private val outputDir = ioConfig.outputDirectory

  private fun copyTestSource(originFileNameWithExtension: String, targetFileNameWithoutExtension: String) {
    val source = Path("../test-files/$originFileNameWithExtension")
    val extension = source.extension
    val targetPath = source.copyTo(outputDir / "$targetFileNameWithoutExtension.$extension")
    targetPath.toFile().deleteOnExit() // this is having problem
  }

  private val mockDownloader = DummyCliDownloader(downloadConfig, tika, environmentConfig, objectMapper)

  @BeforeAll
  fun prepareFiles() {
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
  }

  @ParameterizedTest(name = "recognized downloaded files for song{0}")
  @ValueSource(ints = [1, 2, 3, 4])
  fun recognizedDownloadedFilesForSong(number: Int) {
    val (pvFile, audioFile, thumbnailFile) =
      mockDownloader.tryDownload("fake url", "【vocalist】song$number【producer】[39393]", outputDir)

    val pvExists = pvFile?.exists() == true
    val audioExists = audioFile?.exists() == true

    if (number == 2) {
      assertThat(pvExists && audioExists)
        .describedAs("pvFile and audioFile should exist")
        .isTrue()
    } else {
      assertThat(pvExists || audioExists)
        .describedAs("pvFile or audioFile should exist")
        .isTrue()
    }

    assertThat(thumbnailFile.exists())
      .describedAs("thumbnailFile should exist")
      .isTrue()
  }

  @Test
  fun throwExceptionAboutNotFindingFile() {
    val exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException::class.java) {
      mockDownloader.tryDownload("fake url", "【vocalist】song doesn't exist【producer】[39393]", outputDir)
    }
    assertThat(exception.message).contains("None of")
  }
}

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
  override val targetPvService: PVService = PVService.NICONICODOUGA
}
