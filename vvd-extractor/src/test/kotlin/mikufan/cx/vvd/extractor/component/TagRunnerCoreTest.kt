package mikufan.cx.vvd.extractor.component

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.string.shouldContainIgnoringCase
import io.mockk.every
import io.mockk.mockk
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.commonkt.naming.SongProperFileName
import mikufan.cx.vvd.extractor.component.tagger.base.BaseAudioTagger

class TagRunnerCoreTest(
  // a working example to inject a mock bean with qualifier
//  @field:MockkBean(name = "m4aAudioTagger") // this let mockk-spring framework knows the bean
//  @Qualifier("m4aAudioTagger") // this let spring framework know the bean
//  private val mockTagger: BaseAudioTagger,// because we want to access the bean, we have to declare it on constructor,
) : ShouldSpec({

  val tagRunnerCore = TagRunnerCore()
  val mockTagger = mockk<BaseAudioTagger>() {
    every { name } returns "Mocked M4aAudioTagger"
  }

  context("tag runner core") {
    should("success when tagger success") {
      mockTagger.apply {
        every { tag(any(), any()) } returns Result.success(Unit)
      }
      tagRunnerCore.doTagging(mockTagger, mockk(), mockk(), 3, SongProperFileName("test song 1"))
    }

    should("throw when all attempts failed") {
      mockTagger.apply {
        every { tag(any(), any()) } returns Result.failure(RuntimeException("Mocked failure"))
      }
      val testSongName = "test song 2"
      val exp = shouldThrow<RuntimeVocaloidException> {
        tagRunnerCore.doTagging(mockTagger, mockk(), mockk(), 3, SongProperFileName(testSongName))
      }
      exp.message shouldContainIgnoringCase "All extraction attempt on test song 2 by Mocked M4aAudioTagger failed"
    }
  }
})
