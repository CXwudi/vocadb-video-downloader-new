package mikufan.cx.vvd.extractor.component.extractor.base

import io.kotest.assertions.fail
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldContain
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

  context("exception handling") {
    should("works") {
      FakeAudioExtractor().extract(Path("fake file"), buildFakeTask("fake song"), outputDir)
        .onSuccess {
          fail("should not be here")
        }.onFailure {
          it should beInstanceOf<RuntimeVocaloidException>()
          it.message shouldContain "fake exception"
        }
    }
  }
})

private class FakeAudioExtractor : BaseAudioExtractor() {
  /**
   * the name of the audio extractor
   */
  override val name = "fake audio extractor"

  override fun tryExtract(inputPvFile: Path, baseOutputFileName: String, outputDirectory: Path): Path {
    throw RuntimeVocaloidException("fake exception")
  }
}
