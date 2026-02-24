package mikufan.cx.vvd.downloader.component.downloader.base

import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.commonkt.vocadb.api.model.PVContract
import mikufan.cx.vvd.commonkt.vocadb.api.model.PVService
import mikufan.cx.vvd.commonkt.vocadb.api.model.SongForApiContract
import mikufan.cx.vvd.downloader.config.IOConfig
import mikufan.cx.vvd.downloader.model.Parameters
import mikufan.cx.vvd.downloader.model.VSongTask
import mikufan.cx.vvd.downloader.util.SpringBootTestWithTestProfile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.copyTo
import kotlin.io.path.div
import kotlin.io.path.extension

/**
 * just to test the [BaseDownloader.download] logic without considering the [BaseDownloader.tryDownload]
 */
@SpringBootTestWithTestProfile
class BaseDownloaderTest(
  ioConfig: IOConfig
) {

  private val outputDir = ioConfig.outputDirectory

  private fun buildFakeTask(songName: String): VSongTask {
    val fakeSong = SongForApiContract(
      id = 39393,
      defaultName = songName,
      artistString = "producer feat. vocalist"
    )
    return VSongTask(VSongLabel.builder().build(), Parameters(fakeSong))
  }

  private val fakePv = PVContract(url = "https://fake.url")

  private fun copyTestSource(originFileNameWithExtension: String, targetFileNameWithoutExtension: String) {
    val source = Path("../test-files/$originFileNameWithExtension")
    val extension = source.extension
    val targetPath = source.copyTo(outputDir / "$targetFileNameWithoutExtension.$extension")
    targetPath.toFile().deleteOnExit() // this is having problem
  }

  @Test
  fun properlyHandleRenamingOfSong() {
    val fakeDownloader = object : BaseDownloader() {
      override val downloaderName: String = "FakeDownloader"
      override val targetPvService: PVService = PVService.NOTHING
      override fun tryDownload(url: String, baseFileName: String, outputDirectory: Path): DownloadFiles {
        return DownloadFiles(
          outputDirectory.resolve("$baseFileName.mp4"),
          outputDirectory.resolve("$baseFileName.audio.m4a"),
          outputDirectory.resolve("$baseFileName.thumbnail.jpg")
        )
      }
    }

    copyTestSource(
      "【世界计划】KAITO&MEIKO「ニジイロストーリーズ」 (Another Vocal版) [459823810_part1].f6-trim.mp4",
      "【vocalist】OSTER project song【producer】[39393]-downloading"
    )
    copyTestSource(
      "【世界计划】KAITO&MEIKO「ニジイロストーリーズ」 (Another Vocal版) [459823810_part1].f2-trim.m4a",
      "【vocalist】OSTER project song【producer】[39393]-downloading.audio"
    )
    copyTestSource(
      "【世界计划】KAITO&MEIKO「ニジイロストーリーズ」 (Another Vocal版) [459823810_part1].jpg",
      "【vocalist】OSTER project song【producer】[39393]-downloading.thumbnail"
    )

    val fakeTask = buildFakeTask("OSTER project song")
    fakeDownloader.download(fakePv, fakeTask, outputDir)
      .onFailure {
        fail("should not fail")
      }.onSuccess {
        val (pvFile, audioFile, thumbnailFile) = it
        assertThat(pvFile).isNotNull()
        pvFile!!.let { file ->
          assertThat(file.fileName.toString()).endsWith("-pv.${file.extension}")
        }
        assertThat(audioFile).isNotNull()
        audioFile!!.let { file ->
          assertThat(file.fileName.toString()).endsWith("-audio.${file.extension}")
        }
        assertThat(thumbnailFile.fileName.toString()).endsWith("-thumbnail.${thumbnailFile.extension}")
      }
  }

  @Test
  fun handleException() {
    val fakeDownloader = object : BaseDownloader() {
      override val downloaderName: String = "FakeDownloader"
      override val targetPvService: PVService = PVService.NOTHING
      override fun tryDownload(url: String, baseFileName: String, outputDirectory: Path): DownloadFiles {
        throw RuntimeVocaloidException("a fake exception")
      }
    }

    val fakeTask = buildFakeTask("OSTER project song")
    fakeDownloader.download(fakePv, fakeTask, outputDir)
      .onFailure {
        assertThat(it.message).contains("a fake exception")
      }.onSuccess {
        fail("should not succeed")
      }
  }
}
