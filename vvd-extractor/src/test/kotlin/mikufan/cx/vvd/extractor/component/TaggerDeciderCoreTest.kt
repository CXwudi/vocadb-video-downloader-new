package mikufan.cx.vvd.extractor.component

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.extractor.component.extractor.base.BaseAudioExtractor
import mikufan.cx.vvd.extractor.component.extractor.impl.AacToM4aAudioExtractor
import mikufan.cx.vvd.extractor.component.extractor.impl.AnyToMkaAudioExtractor
import mikufan.cx.vvd.extractor.component.extractor.impl.OpusToOggAudioExtractor
import mikufan.cx.vvd.extractor.component.tagger.impl.M4aAudioTagger
import mikufan.cx.vvd.extractor.component.tagger.impl.MkaAudioTagger
import mikufan.cx.vvd.extractor.component.tagger.impl.Mp3AudioTagger
import mikufan.cx.vvd.extractor.component.tagger.impl.OggOpusAudioTagger
import mikufan.cx.vvd.extractor.component.util.MediaFormatChecker
import mikufan.cx.vvd.extractor.util.AudioMediaFormat
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import java.util.Optional
import kotlin.io.path.Path

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TaggerDeciderCoreTest {

  private val fakeM4aFile = Path("fake.m4a")
  private val fakeOpusFile = Path("fake.opus")
  private val fakeMp3File = Path("fake.mp3")

  data class RawFormatCase(
    val name: String,
    val file: java.nio.file.Path,
    val expectedClass: Class<*>
  ) {
    override fun toString(): String = name
  }

  private val createMockChecker = {
    mockk<MediaFormatChecker>(relaxed = true) {
      every { checkAudioFormat(any()) } answers {
        val mediaFileName = args[0].toString()
        when {
          mediaFileName.endsWith("m4a") -> AudioMediaFormat.AAC
          mediaFileName.endsWith("opus") -> AudioMediaFormat.OPUS
          mediaFileName.endsWith("mp3") -> AudioMediaFormat.MPEG_AUDIO
          else -> "unknown"
        }
      }
    }
  }

  private val createMockFactory = {
    mockk<ApplicationContext>() {
      every { getBean<M4aAudioTagger>() } returns mockk<M4aAudioTagger>(relaxed = true)
      every { getBean<OggOpusAudioTagger>() } returns mockk<OggOpusAudioTagger>(relaxed = true)
      every { getBean<Mp3AudioTagger>() } returns mockk<Mp3AudioTagger>(relaxed = true)
      every { getBean<MkaAudioTagger>() } returns mockk<MkaAudioTagger>(relaxed = true)
    }
  }

  @Test
  fun decideM4aTaggerForAacToM4aExtractor() {
    val mockAudioFormatChecker = createMockChecker()
    val mockBeanFactory = createMockFactory()
    val taggerDecider = TaggerDeciderCore(mockAudioFormatChecker, mockBeanFactory)

    val tagger = taggerDecider.decideTagger(Optional.of(mockk<AacToM4aAudioExtractor>(relaxed = true)), null)
    assertThat(tagger).isInstanceOf(M4aAudioTagger::class.java)
    verify(exactly = 0) { mockAudioFormatChecker.checkAudioFormat(any()) }
  }

  @Test
  fun decideOggTaggerForOpusToOggExtractor() {
    val mockAudioFormatChecker = createMockChecker()
    val mockBeanFactory = createMockFactory()
    val taggerDecider = TaggerDeciderCore(mockAudioFormatChecker, mockBeanFactory)

    val tagger = taggerDecider.decideTagger(Optional.of(mockk<OpusToOggAudioExtractor>(relaxed = true)), null)
    assertThat(tagger).isInstanceOf(OggOpusAudioTagger::class.java)
    verify(exactly = 0) { mockAudioFormatChecker.checkAudioFormat(any()) }
  }

  @Test
  fun decideMkaTaggerForAnyToMkaExtractor() {
    val mockAudioFormatChecker = createMockChecker()
    val mockBeanFactory = createMockFactory()
    val taggerDecider = TaggerDeciderCore(mockAudioFormatChecker, mockBeanFactory)

    val tagger = taggerDecider.decideTagger(Optional.of(mockk<AnyToMkaAudioExtractor>(relaxed = true)), null)
    assertThat(tagger).isInstanceOf(MkaAudioTagger::class.java)
    verify(exactly = 0) { mockAudioFormatChecker.checkAudioFormat(any()) }
  }

  @Test
  fun throwExceptionForUnknownExtractor() {
    val mockAudioFormatChecker = createMockChecker()
    val mockBeanFactory = createMockFactory()
    val taggerDecider = TaggerDeciderCore(mockAudioFormatChecker, mockBeanFactory)
    val unknownExtractor = mockk<BaseAudioExtractor>(relaxed = true) {
      every { name } returns "Unknown Audio Extractor"
    }

    assertThatThrownBy { taggerDecider.decideTagger(Optional.of(unknownExtractor), null) }
      .isInstanceOf(IllegalStateException::class.java)
      .hasMessageContaining("This should not happened, unknown audio extractor: Unknown Audio Extractor")
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("rawFormatCases")
  fun decideTaggerForRawFile(case: RawFormatCase) {
    val mockAudioFormatChecker = createMockChecker()
    val mockBeanFactory = createMockFactory()
    val taggerDecider = TaggerDeciderCore(mockAudioFormatChecker, mockBeanFactory)

    val tagger = taggerDecider.decideTagger(Optional.empty(), case.file)
    assertThat(tagger).isInstanceOf(case.expectedClass)
    verify(exactly = 1) { mockAudioFormatChecker.checkAudioFormat(case.file) }
  }

  fun rawFormatCases(): List<RawFormatCase> = listOf(
    RawFormatCase("m4a", fakeM4aFile, M4aAudioTagger::class.java),
    RawFormatCase("opus", fakeOpusFile, OggOpusAudioTagger::class.java),
    RawFormatCase("mp3", fakeMp3File, Mp3AudioTagger::class.java)
  )

  @Test
  fun throwExceptionForUnknownAudioFormat() {
    val mockAudioFormatChecker = mockk<MediaFormatChecker>(relaxed = true) {
      every { checkAudioFormat(any()) } returns "unknown"
    }
    val mockBeanFactory = createMockFactory()
    val taggerDecider = TaggerDeciderCore(mockAudioFormatChecker, mockBeanFactory)

    assertThatThrownBy { taggerDecider.decideTagger(Optional.empty(), fakeM4aFile) }
      .isInstanceOf(RuntimeVocaloidException::class.java)
      .hasMessageContaining("Audio format unknown is not supported from fake.m4a")
  }
}
