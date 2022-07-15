package mikufan.cx.vvd.extractor.component.extractor.base

import io.kotest.assertions.fail
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.types.beInstanceOf
import mikufan.cx.vocadbapiclient.model.SongForApiContract
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.extractor.config.IOConfig
import mikufan.cx.vvd.extractor.model.Parameters
import mikufan.cx.vvd.extractor.model.VSongTask
import mikufan.cx.vvd.extractor.util.SpringBootDirtyTestWithTestProfile
import mikufan.cx.vvd.extractor.util.SpringShouldSpec
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.copyTo
import kotlin.io.path.div
import kotlin.io.path.extension

@SpringBootDirtyTestWithTestProfile
class BaseAudioExtractorTest(
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

  val copyTestSource = fun(originFileNameWithExtension: String, targetFileNameWithoutExtension: String) {
    val source = Path("../test-files/$originFileNameWithExtension")
    val extension = source.extension
    val targetPath = source.copyTo(outputDir / "$targetFileNameWithoutExtension.$extension")
    targetPath.toFile().deleteOnExit() // this is having problem
  }

  context("assume success extraction") {
    val fakeExtractor = object : BaseAudioExtractor() {
      override val name = "fake audio extractor"
      override fun tryExtract(inputPvFile: Path, baseFileName: String, outputDirectory: Path): Path {
        return outputDirectory.resolve("$baseFileName.m4a")
      }
    }

    copyTestSource(
      "【世界计划】KAITO&MEIKO「ニジイロストーリーズ」 (Another Vocal版) [459823810_part1].f2-trim.m4a",
      "【vocalist】OSTER project song【producer】[39393]-i0"
    )

    should("properly handle renaming of the extracted audio") {
      val fakeTask = buildFakeTask("OSTER project song")
      fakeExtractor.extract(Path("whatever"), fakeTask, outputDir).onFailure {
        fail("should not fail")
      }.onSuccess {
        it.fileName.toString() shouldEndWith "-audio.${it.extension}"
      }
    }
  }

  context("exception handling") {
    should("works") {
      val fakeExtractor = object : BaseAudioExtractor() {
        override val name = "fake audio extractor"
        override fun tryExtract(inputPvFile: Path, baseFileName: String, outputDirectory: Path): Path {
          throw RuntimeVocaloidException("fake exception")
        }
      }
      fakeExtractor.extract(Path("fake file"), buildFakeTask("fake song"), outputDir)
        .onSuccess {
          fail("should not be here")
        }.onFailure {
          it should beInstanceOf<RuntimeVocaloidException>()
          it.message shouldContain "fake exception"
        }
    }
  }
})
