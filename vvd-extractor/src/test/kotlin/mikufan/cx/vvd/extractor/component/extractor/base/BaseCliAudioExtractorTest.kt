package mikufan.cx.vvd.extractor.component.extractor.base

import mikufan.cx.vvd.extractor.config.IOConfig
import mikufan.cx.vvd.extractor.config.ProcessConfig
import mikufan.cx.vvd.extractor.util.SpringBootTestWithTestProfile
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BaseCliAudioExtractorTest(
  ioConfig: IOConfig,
  processConfig: ProcessConfig,
  @Qualifier("extractorThreadPool") threadPool: ThreadPoolExecutor
) {
  private val outputDir = ioConfig.outputDirectory

  private fun copyTestSource(originFileNameWithExtension: String, targetFileNameWithoutExtension: String) {
    val source = Path("../test-files/$originFileNameWithExtension")
    val extension = source.extension
    val targetPath = source.copyTo(outputDir / "$targetFileNameWithoutExtension.$extension")
    targetPath.toFile().deleteOnExit() // this is having problem
  }

  private val dummyAudioExtractor = DummyCliAudioExtractor(processConfig, threadPool)

  @BeforeAll
  fun prepareFiles() {
    copyTestSource("「クリーデンス」／霧島feat.初音ミク-sm39825313-trim.ts", "【vocalist】song1【producer】[39393]")
    copyTestSource(
      "【初音ミク】LONELY POP feat.初音ミク (Yandere VIP Remix)【オリジナル】 [sm40260101]-trim.mp4",
      "【vocalist】song2【producer】[39393]"
    )
    copyTestSource("Kikuo - 幽体離脱 [UHH2KKN0xoc]-trim.webm", "【vocalist】song3【producer】[39393]")
  }

  @ParameterizedTest(name = "found the new file for song{0}")
  @ValueSource(ints = [1, 2, 3])
  fun foundNewFileForSong(number: Int) {
    val result = dummyAudioExtractor.tryExtract(
      Path("some input file"),
      "【vocalist】song$number【producer】[39393]",
      outputDir
    )
    assertThat(result.fileName.toString()).startsWith("【vocalist】song$number【producer】[39393]")
  }

  @Test
  fun throwOnNotFindingExtractedAudioFile() {
    val exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException::class.java) {
      dummyAudioExtractor.tryExtract(Path("some input file"), "【vocalist】song4【producer】[39393]", outputDir)
    }
    assertThat(exception.message).startsWith("No extracted audio file that contain")
  }
}

// can't use mockk for this as we need actual calling of protected method
class DummyCliAudioExtractor(
  processConfig: ProcessConfig,
  threadPool: ThreadPoolExecutor
) : BaseCliAudioExtractor(processConfig, threadPool) {

  override fun buildCommand(inputPvFile: Path, baseOutputFileName: String, outputDirectory: Path): List<String> =
    listOf("java", "--version")

  override val name: String = "dummy cli audio extractor"
}
