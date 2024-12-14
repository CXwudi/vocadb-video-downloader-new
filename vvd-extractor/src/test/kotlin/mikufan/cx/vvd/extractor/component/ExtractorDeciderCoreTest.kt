package mikufan.cx.vvd.extractor.component

import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.extractor.component.extractor.base.BaseAudioExtractor
import mikufan.cx.vvd.extractor.component.extractor.impl.AacToM4aAudioExtractor
import mikufan.cx.vvd.extractor.component.extractor.impl.OpusToOggAudioExtractor
import mikufan.cx.vvd.extractor.config.IOConfig
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import java.nio.file.Files
import kotlin.io.path.createFile
import kotlin.io.path.deleteExisting
import kotlin.io.path.div

class ExtractorDeciderCoreTest : ShouldSpec({

  context("extractor decider") {
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
    }

    should("not set audio extractor if using audio file") {
      val audioFileName = "$baseInputFileName.aac"
      val audioFile = tempInputDir / audioFileName
      audioFile.createFile()

      val extractorDeciderCore = ExtractorDeciderCore(ioConfig, mockk(), mockk())

      val decideExtractor: BaseAudioExtractor? = extractorDeciderCore.decideExtractor(audioFileName, "", baseInputFileName)

      decideExtractor.shouldBeNull()
      audioFile.deleteExisting()
    }

    context("on pv files") {
      listOf("aac", "opus").forEach { format ->
        val mockChecker = mockk<MediaFormatChecker> {
          every { checkAudioFormat(any()) } returns format
        }

        val pvFileName = "$baseInputFileName.mp4"
        val pvFile = tempInputDir / pvFileName
        pvFile.createFile()

        should("set the correct extractor for $format format") {
          val extractorDeciderCore = ExtractorDeciderCore(ioConfig, mockChecker, mockCtx)
          val decideExtractor: BaseAudioExtractor? = extractorDeciderCore.decideExtractor("", pvFileName, baseInputFileName)
          decideExtractor.shouldNotBeNull()
          when (format) {
            "aac" -> decideExtractor.shouldBeInstanceOf<AacToM4aAudioExtractor>()
            "opus" -> decideExtractor.shouldBeInstanceOf<OpusToOggAudioExtractor>()
            else -> fail("Unknown format $format")
          }
        }
        pvFile.deleteExisting()
      }
    }

    should("fails if encounter an unknown pv file format") {
      val mockChecker = mockk<MediaFormatChecker> {
        every { checkAudioFormat(any()) } returns "wired format"
      }

      val pvFileName = "$baseInputFileName.mp4"
      val pvFile = tempInputDir / pvFileName
      pvFile.createFile()

      val extractorDeciderCore = ExtractorDeciderCore(ioConfig, mockChecker, mockCtx)
      val exception = shouldThrow<RuntimeVocaloidException> {
        extractorDeciderCore.decideExtractor("", pvFileName, baseInputFileName)
      }
      exception.message shouldContain "Unsupported audio format"

      pvFile.deleteExisting()
    }

    should("fails if neither audio file nor pv file exists") {
      val extractorDeciderCore = ExtractorDeciderCore(ioConfig, mockk(), mockk())
      val exception = shouldThrow<RuntimeVocaloidException> {
        extractorDeciderCore.decideExtractor("fake.mp3", "fake.mp4", baseInputFileName)
      }

      exception.message shouldContain "pv file not found"
    }
  }
})