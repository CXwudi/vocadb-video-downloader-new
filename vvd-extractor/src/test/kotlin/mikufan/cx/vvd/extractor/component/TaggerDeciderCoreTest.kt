package mikufan.cx.vvd.extractor.component

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.extractor.component.extractor.base.BaseAudioExtractor
import mikufan.cx.vvd.extractor.component.extractor.impl.AacToM4aAudioExtractor
import mikufan.cx.vvd.extractor.component.extractor.impl.OpusToOggAudioExtractor
import mikufan.cx.vvd.extractor.component.tagger.impl.M4aAudioTagger
import mikufan.cx.vvd.extractor.component.tagger.impl.Mp3AudioTagger
import mikufan.cx.vvd.extractor.component.tagger.impl.OggOpusAudioTagger
import mikufan.cx.vvd.extractor.component.util.MediaFormatChecker
import mikufan.cx.vvd.extractor.util.AudioMediaFormat
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.name

class TaggerDeciderCoreTest : ShouldSpec({

  val fakeM4aFile = Path("fake.m4a")
  val fakeOpusFile = Path("fake.opus")
  val fakeMp3File = Path("fake.mp3")

  val createMockChecker = {
    mockk<MediaFormatChecker>(relaxed = true) {
      every { checkAudioFormat(coMatch { it.name.endsWith("m4a") }) } returns AudioMediaFormat.AAC
      every { checkAudioFormat(coMatch { it.name.endsWith("opus") }) } returns AudioMediaFormat.OPUS
      every { checkAudioFormat(coMatch { it.name.endsWith("mp3") }) } returns AudioMediaFormat.MPEG_AUDIO
    }
  }

  val createMockFactory = {
    mockk<ApplicationContext>() {
      every { getBean<M4aAudioTagger>() } returns mockk<M4aAudioTagger>(relaxed = true)
      every { getBean<OggOpusAudioTagger>() } returns mockk<OggOpusAudioTagger>(relaxed = true)
      every { getBean<Mp3AudioTagger>() } returns mockk<Mp3AudioTagger>(relaxed = true)
    }
  }


  context("tag decider") {
    should("decide m4a tagger for m4a file produced by aac->m4a extractor") {
      val mockAudioFormatChecker = createMockChecker()
      val mockBeanFactory = createMockFactory()
      val taggerDecider = TaggerDeciderCore(mockAudioFormatChecker, mockBeanFactory)

      val tagger = taggerDecider.decideTagger(Optional.of(mockk<AacToM4aAudioExtractor>(relaxed = true)), null)
      tagger.shouldBeInstanceOf<M4aAudioTagger>()
      coVerify(exactly = 0) { mockAudioFormatChecker.checkAudioFormat(any()) }
    }
    should("decide ogg tagger for opus file produced by opus->ogg extractor") {
      val mockAudioFormatChecker = createMockChecker()
      val mockBeanFactory = createMockFactory()
      val taggerDecider = TaggerDeciderCore(mockAudioFormatChecker, mockBeanFactory)

      val tagger = taggerDecider.decideTagger(Optional.of(mockk<OpusToOggAudioExtractor>(relaxed = true)), null)
      tagger.shouldBeInstanceOf<OggOpusAudioTagger>()
      coVerify(exactly = 0) { mockAudioFormatChecker.checkAudioFormat(any()) }
    }
    should("throw exception for unknown extractor") {
      val mockAudioFormatChecker = createMockChecker()
      val mockBeanFactory = createMockFactory()
      val taggerDecider = TaggerDeciderCore(mockAudioFormatChecker, mockBeanFactory)
      val unknownExtractor = mockk<BaseAudioExtractor>(relaxed = true) {
        every { name } returns "Unknown Audio Extractor"
      }

      shouldThrow<IllegalStateException> {
        taggerDecider.decideTagger(Optional.of(unknownExtractor), null)
      }.message shouldContain "This should not happened, unknown audio extractor: Unknown Audio Extractor"
    }
    should("decide m4a tagger for raw m4a file") {
      val mockAudioFormatChecker = createMockChecker()
      val mockBeanFactory = createMockFactory()
      val taggerDecider = TaggerDeciderCore(mockAudioFormatChecker, mockBeanFactory)

      val tagger = taggerDecider.decideTagger(Optional.empty(), fakeM4aFile)
      tagger.shouldBeInstanceOf<M4aAudioTagger>()
      coVerify(exactly = 1) { mockAudioFormatChecker.checkAudioFormat(fakeM4aFile) }
    }
    should("decide ogg tagger for raw opus file") {
      val mockAudioFormatChecker = createMockChecker()
      val mockBeanFactory = createMockFactory()
      val taggerDecider = TaggerDeciderCore(mockAudioFormatChecker, mockBeanFactory)

      val tagger = taggerDecider.decideTagger(Optional.empty(), fakeOpusFile)
      tagger.shouldBeInstanceOf<OggOpusAudioTagger>()
      coVerify(exactly = 1) { mockAudioFormatChecker.checkAudioFormat(fakeOpusFile) }
    }
    should("decide mp3 tagger for raw mp3 file") {
      val mockAudioFormatChecker = createMockChecker()
      val mockBeanFactory = createMockFactory()
      val taggerDecider = TaggerDeciderCore(mockAudioFormatChecker, mockBeanFactory)

      val tagger = taggerDecider.decideTagger(Optional.empty(), fakeMp3File)
      tagger.shouldBeInstanceOf<Mp3AudioTagger>()
      coVerify(exactly = 1) { mockAudioFormatChecker.checkAudioFormat(fakeMp3File) }
    }
    should("throw exception for unknown audio format") {
      val mockAudioFormatChecker = mockk<MediaFormatChecker>(relaxed = true) {
        every { checkAudioFormat(any()) } returns "unknown"
      }
      val mockBeanFactory = createMockFactory()
      val taggerDecider = TaggerDeciderCore(mockAudioFormatChecker, mockBeanFactory)

      shouldThrow<RuntimeVocaloidException> {
        taggerDecider.decideTagger(Optional.empty(), fakeM4aFile)
      }.message shouldContain "Audio format unknown is not supported from fake.m4a"
    }
  }
})
