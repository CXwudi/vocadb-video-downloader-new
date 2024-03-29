package mikufan.cx.vvd.extractor.component.extractor.base

import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import mikufan.cx.vvd.extractor.config.IOConfig
import mikufan.cx.vvd.extractor.config.ProcessConfig
import mikufan.cx.vvd.extractor.util.SpringBootDirtyTestWithTestProfile
import mikufan.cx.vvd.extractor.util.SpringBootTestWithTestProfile
import mikufan.cx.vvd.extractor.util.SpringShouldSpec
import org.springframework.beans.factory.annotation.Qualifier
import java.nio.file.Path
import java.util.concurrent.ThreadPoolExecutor
import kotlin.io.path.Path
import kotlin.io.path.copyTo
import kotlin.io.path.div
import kotlin.io.path.extension
/**
 * This test only makes sure that the main logic [BaseCliAudioExtractor.tryExtract] is working
 *
 * No actual extraction is done,
 * test environment is set up by moving some test music files to the target output directory
 * and trading it as the actual extraction output.
 */
@SpringBootTestWithTestProfile
class BaseCliAudioExtractorTest(
  ioConfig: IOConfig,
  processConfig: ProcessConfig,
  @Qualifier("extractorThreadPool") threadPool: ThreadPoolExecutor
) : SpringShouldSpec({
  val outputDir = ioConfig.outputDirectory

  val copyTestSource = fun(originFileNameWithExtension: String, targetFileNameWithoutExtension: String) {
    val source = Path("../test-files/$originFileNameWithExtension")
    val extension = source.extension
    val targetPath = source.copyTo(outputDir / "$targetFileNameWithoutExtension.$extension")
    targetPath.toFile().deleteOnExit() // this is having problem
  }

  val dummyAudioExtractor = DummyCliAudioExtractor(processConfig, threadPool)
  context("assume success extraction") {
    copyTestSource("「クリーデンス」／霧島feat.初音ミク-sm39825313-trim.ts", "【vocalist】song1【producer】[39393]")
    copyTestSource(
      "【初音ミク】LONELY POP feat.初音ミク (Yandere VIP Remix)【オリジナル】 [sm40260101]-trim.mp4",
      "【vocalist】song2【producer】[39393]"
    )
    copyTestSource("Kikuo - 幽体離脱 [UHH2KKN0xoc]-trim.webm", "【vocalist】song3【producer】[39393]")

    for (number in 1..3) {
      should("found the new file for song$number") {
        runCatching {
          dummyAudioExtractor.tryExtract(Path("some input file"), "【vocalist】song$number【producer】[39393]", outputDir)
        }.onFailure {
          fail("should not fail")
        }.onSuccess {
          it.fileName.toString() shouldStartWith "【vocalist】song$number【producer】[39393]"
        }
      }
    }
  }

  context("assume failed extraction") {
    should("throw on not finding extracted audio file") {
      runCatching {
        dummyAudioExtractor.tryExtract(Path("some input file"), "【vocalist】song4【producer】[39393]", outputDir)
      }.onSuccess {
        fail("should not success")
      }.onFailure {
        it::class.java shouldBe IllegalStateException::class.java
        it.message shouldStartWith "No extracted audio file that contain"
      }
    }
  }
})

// can't use mockk for this as we need actual calling of protected method
class DummyCliAudioExtractor(
  processConfig: ProcessConfig,
  threadPool: ThreadPoolExecutor
) : BaseCliAudioExtractor(processConfig, threadPool) {

  override fun buildCommand(inputPvFile: Path, baseOutputFileName: String, outputDirectory: Path): List<String> =
    listOf("java", "--version")

  override val name: String = "dummy cli audio extractor"
}
