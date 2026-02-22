package mikufan.cx.vvd.extractor.component.extractor.base

import mikufan.cx.vvd.commonkt.vocadb.api.model.SongForApiContract
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.extractor.config.IOConfig
import mikufan.cx.vvd.extractor.model.Parameters
import mikufan.cx.vvd.extractor.model.VSongTask
import mikufan.cx.vvd.extractor.util.SpringBootTestWithTestProfile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.copyTo
import kotlin.io.path.div
import kotlin.io.path.extension

@SpringBootTestWithTestProfile
class BaseAudioExtractorTest(
  ioConfig: IOConfig,
) {

  private val outputDir = ioConfig.outputDirectory

  private val buildFakeTask = fun(songName: String): VSongTask {
    val fakeSong = SongForApiContract(
      defaultName = songName,
      artistString = "producer feat. vocalist",
      id = 39393
    )
    return VSongTask(VSongLabel.builder().build(), Parameters(fakeSong))
  }

  private fun copyTestSource(originFileNameWithExtension: String, targetFileNameWithoutExtension: String) {
    val source = Path("../test-files/$originFileNameWithExtension")
    val extension = source.extension
    val targetPath = source.copyTo(outputDir / "$targetFileNameWithoutExtension.$extension")
    targetPath.toFile().deleteOnExit() // this is having problem
  }

  @Test
  fun properlyHandleRenamingOfExtractedAudio() {
    val fakeExtractor = object : BaseAudioExtractor() {
      override val name = "fake audio extractor"
      override fun tryExtract(inputPvFile: Path, baseOutputFileName: String, outputDirectory: Path): Path {
        return outputDirectory.resolve("$baseOutputFileName.m4a")
      }
    }

    copyTestSource(
      "【世界计划】KAITO&MEIKO「ニジイロストーリーズ」 (Another Vocal版) [459823810_part1].f2-trim.m4a",
      "【vocalist】OSTER project song【producer】[39393]-extracting"
    )

    val fakeTask = buildFakeTask("OSTER project song")
    fakeExtractor.extract(Path("whatever"), fakeTask, outputDir).onFailure {
      fail("should not fail")
    }.onSuccess {
      assertThat(it.fileName.toString()).endsWith("-audio.${it.extension}")
    }
  }

  @Test
  fun exceptionHandlingWorks() {
    val fakeExtractor = object : BaseAudioExtractor() {
      override val name = "fake audio extractor"
      override fun tryExtract(inputPvFile: Path, baseOutputFileName: String, outputDirectory: Path): Path {
        throw RuntimeVocaloidException("fake exception")
      }
    }

    fakeExtractor.extract(Path("fake file"), buildFakeTask("fake song"), outputDir)
      .onSuccess {
        fail("should not be here")
      }.onFailure {
        assertThat(it).isInstanceOf(RuntimeVocaloidException::class.java)
        assertThat(it.message).contains("fake exception")
      }
  }
}
