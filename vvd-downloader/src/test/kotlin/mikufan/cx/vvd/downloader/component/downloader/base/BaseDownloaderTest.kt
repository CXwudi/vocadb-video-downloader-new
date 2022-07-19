package mikufan.cx.vvd.downloader.component.downloader.base

import io.kotest.assertions.fail
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith
import mikufan.cx.vocadbapiclient.model.PVContract
import mikufan.cx.vocadbapiclient.model.SongForApiContract
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.commonkt.vocadb.PVServicesEnum
import mikufan.cx.vvd.downloader.config.IOConfig
import mikufan.cx.vvd.downloader.model.Parameters
import mikufan.cx.vvd.downloader.model.VSongTask
import mikufan.cx.vvd.downloader.util.SpringBootDirtyTestWithTestProfile
import mikufan.cx.vvd.downloader.util.SpringShouldSpec
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.copyTo
import kotlin.io.path.div
import kotlin.io.path.extension

/**
 * just to test the [BaseDownloader.download] logic without considering the [BaseDownloader.tryDownload]
 */
@SpringBootDirtyTestWithTestProfile
class BaseDownloaderTest(
  ioConfig: IOConfig
) : SpringShouldSpec({

  val outputDir = ioConfig.outputDirectory

  val buildFakeTask = fun(soneName: String): VSongTask {
    val fakeSong = SongForApiContract().apply {
      this.defaultName = soneName
      artistString = "producer feat. vocalist"
      id = 39393
    }
    val fakeTask = VSongTask(VSongLabel.builder().build(), Parameters(fakeSong))
    return fakeTask
  }
  val fakePv = PVContract().apply {
    url = "https://fake.url"
  }

  val copyTestSource = fun(originFileNameWithExtension: String, targetFileNameWithoutExtension: String) {
    val source = Path("../test-files/$originFileNameWithExtension")
    val extension = source.extension
    val targetPath = source.copyTo(outputDir / "$targetFileNameWithoutExtension.$extension")
    targetPath.toFile().deleteOnExit() // this is having problem
  }

  context("assume download success") {
    val fakeDownloader = object : BaseDownloader() {
      override val downloaderName: String = "FakeDownloader"
      override val targetPvService: PVServicesEnum = PVServicesEnum.NOTHING
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

    should("properly handle renaming of song") {
      val fakeTask = buildFakeTask("OSTER project song")
      fakeDownloader.download(fakePv, fakeTask, outputDir)
        .onFailure {
          fail("should not fail")
        }.onSuccess {
          val (pvFile, audioFile, thumbnailFile) = it
          pvFile?.let { pvFile ->
            pvFile.fileName.toString() shouldEndWith "-pv.${pvFile.extension}"
          }
          audioFile?.let { audioFile ->
            audioFile.fileName.toString() shouldEndWith "-audio.${audioFile.extension}"
          }
          thumbnailFile.fileName.toString() shouldEndWith "-thumbnail.${thumbnailFile.extension}"
        }
    }
  }

  context("assume download fail") {
    val fakeDownloader = object : BaseDownloader() {
      override val downloaderName: String = "FakeDownloader"
      override val targetPvService: PVServicesEnum = PVServicesEnum.NOTHING
      override fun tryDownload(url: String, baseFileName: String, outputDirectory: Path): DownloadFiles {
        throw RuntimeVocaloidException("a fake exception")
      }
    }

    should("handle exception") {
      val fakeTask = buildFakeTask("OSTER project song")
      fakeDownloader.download(fakePv, fakeTask, outputDir)
        .onFailure {
          it.message shouldContain "a fake exception"
        }.onSuccess {
          fail("should not succeed")
        }
    }
  }
})
