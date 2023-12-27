package mikufan.cx.vvd.extractor.component

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import mikufan.cx.vvd.extractor.component.extractor.impl.AacToM4aAudioExtractor
import mikufan.cx.vvd.extractor.component.extractor.impl.OpusToOggAudioExtractor
import mikufan.cx.vvd.extractor.component.tagger.impl.M4aAudioTagger
import mikufan.cx.vvd.extractor.component.tagger.impl.OggOpusAudioTagger
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.name

class TaggerDeciderCoreTest : ShouldSpec({

  val fakeM4aFile = Path("fake.m4a")
  val fakeOpusFile = Path("fake.opus")

  val createMockChecker = {
    mockk<MediaFormatChecker>(relaxed = true) {
      every { checkAudioFormat(coMatch { it.name.endsWith("m4a") }) } returns "aac"
      every { checkAudioFormat(coMatch { it.name.endsWith("opus") }) } returns "opus"
    }
  }

  val createMockFactory = {
    mockk<ApplicationContext>() {
      every { getBean<M4aAudioTagger>() } returns mockk<M4aAudioTagger>(relaxed = true)
      every { getBean<OggOpusAudioTagger>() } returns mockk<OggOpusAudioTagger>(relaxed = true)
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
  }
})
