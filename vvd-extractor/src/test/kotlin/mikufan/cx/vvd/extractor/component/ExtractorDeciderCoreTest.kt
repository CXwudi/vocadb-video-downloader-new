package mikufan.cx.vvd.extractor.component

import io.mockk.every
import io.mockk.mockk
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.extractor.component.extractor.base.BaseAudioExtractor
import mikufan.cx.vvd.extractor.component.extractor.impl.AacToM4aAudioExtractor
import mikufan.cx.vvd.extractor.component.extractor.impl.AnyToMkaAudioExtractor
import mikufan.cx.vvd.extractor.component.extractor.impl.OpusToOggAudioExtractor
import mikufan.cx.vvd.extractor.component.util.MediaFormatChecker
import mikufan.cx.vvd.extractor.config.IOConfig
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import java.nio.file.Files
import kotlin.io.path.createFile
import kotlin.io.path.deleteExisting
import kotlin.io.path.div

class ExtractorDeciderCoreTest {

  @Test
  fun notSetAudioExtractorIfUsingAudioFile() {
    val tempInputDir = Files.createTempDirectory("extractor-core-test-")
    val ioConfig = mockk<IOConfig> {
      every { inputDirectory } returns tempInputDir
    }
    val baseInputFileName = "fake input file"

    val audioFileName = "$baseInputFileName.aac"
    val audioFile = tempInputDir / audioFileName
    audioFile.createFile()

    val extractorDeciderCore = ExtractorDeciderCore(ioConfig, mockk(), mockk())

    val decideExtractor: BaseAudioExtractor? =
      extractorDeciderCore.decideExtractor(audioFileName, "", baseInputFileName)

    assertThat(decideExtractor).isNull()
    audioFile.deleteExisting()
  }

  @Test
  fun setCorrectExtractorForKnownFormat() {
    val tempInputDir = Files.createTempDirectory("extractor-core-test-")
    val ioConfig = mockk<IOConfig> {
      every { inputDirectory } returns tempInputDir
    }
    val baseInputFileName = "fake input file"

    val mockCtx = mockk<ApplicationContext> {
      every { getBean<AacToM4aAudioExtractor>() } returns mockk {
        every { name } returns "Mock AAC to M4A Audio Extractor"
      }
      every { getBean<OpusToOggAudioExtractor>() } returns mockk {
        every { name } returns "Mock Opus to Ogg Audio Extractor"
      }
      every { getBean<AnyToMkaAudioExtractor>() } returns mockk {
        every { name } returns "Mock Any to Mka Audio Extractor"
      }
    }

    listOf("aac", "opus").forEach { format ->
      val mockChecker = mockk<MediaFormatChecker> {
        every { checkAudioFormat(any()) } returns format
      }

      val pvFileName = "$baseInputFileName.mp4"
      val pvFile = tempInputDir / pvFileName
      pvFile.createFile()

      val extractorDeciderCore = ExtractorDeciderCore(ioConfig, mockChecker, mockCtx)
      val decideExtractor: BaseAudioExtractor? =
        extractorDeciderCore.decideExtractor("", pvFileName, baseInputFileName)

      assertThat(decideExtractor).isNotNull()
      when (format) {
        "aac" -> assertThat(decideExtractor).isInstanceOf(AacToM4aAudioExtractor::class.java)
        "opus" -> assertThat(decideExtractor).isInstanceOf(OpusToOggAudioExtractor::class.java)
      }
      pvFile.deleteExisting()
    }
  }

  @Test
  fun fallbackToMkaExtractorForUnknownFormat() {
    val tempInputDir = Files.createTempDirectory("extractor-core-test-")
    val ioConfig = mockk<IOConfig> {
      every { inputDirectory } returns tempInputDir
    }
    val baseInputFileName = "fake input file"

    val mockCtx = mockk<ApplicationContext> {
      every { getBean<AacToM4aAudioExtractor>() } returns mockk {
        every { name } returns "Mock AAC to M4A Audio Extractor"
      }
      every { getBean<OpusToOggAudioExtractor>() } returns mockk {
        every { name } returns "Mock Opus to Ogg Audio Extractor"
      }
      every { getBean<AnyToMkaAudioExtractor>() } returns mockk {
        every { name } returns "Mock Any to Mka Audio Extractor"
      }
    }

    val mockChecker = mockk<MediaFormatChecker> {
      every { checkAudioFormat(any()) } returns "wired format"
    }

    val pvFileName = "$baseInputFileName.mp4"
    val pvFile = tempInputDir / pvFileName
    pvFile.createFile()

    val extractorDeciderCore = ExtractorDeciderCore(ioConfig, mockChecker, mockCtx)
    val decidedExtractor: BaseAudioExtractor? =
      extractorDeciderCore.decideExtractor("", pvFileName, baseInputFileName)

    assertThat(decidedExtractor).isNotNull()
    assertThat(decidedExtractor).isInstanceOf(AnyToMkaAudioExtractor::class.java)

    pvFile.deleteExisting()
  }

  @Test
  fun failsIfNeitherAudioFileNorPvFileExists() {
    val tempInputDir = Files.createTempDirectory("extractor-core-test-")
    val ioConfig = mockk<IOConfig> {
      every { inputDirectory } returns tempInputDir
    }
    val baseInputFileName = "fake input file"

    val extractorDeciderCore = ExtractorDeciderCore(ioConfig, mockk(), mockk())

    assertThatThrownBy {
      extractorDeciderCore.decideExtractor("fake.mp3", "fake.mp4", baseInputFileName)
    }
      .isInstanceOf(RuntimeVocaloidException::class.java)
      .hasMessageContaining("pv file not found")
  }
}
