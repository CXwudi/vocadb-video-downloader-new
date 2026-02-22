package mikufan.cx.vvd.extractor.component

import io.mockk.every
import io.mockk.mockk
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.commonkt.naming.SongProperFileName
import mikufan.cx.vvd.extractor.component.tagger.base.BaseAudioTagger
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test

class TagRunnerCoreTest {

  @Test
  fun successWhenTaggerSuccess() {
    val tagRunnerCore = TagRunnerCore()
    val mockTagger = mockk<BaseAudioTagger>() {
      every { name } returns "Mocked M4aAudioTagger"
      every { tag(any(), any()) } returns Result.success(Unit)
    }

    assertDoesNotThrow {
      tagRunnerCore.doTagging(mockTagger, mockk(), mockk(), 3, SongProperFileName("test song 1"))
    }
  }

  @Test
  fun throwWhenAllAttemptsFailed() {
    val tagRunnerCore = TagRunnerCore()
    val mockTagger = mockk<BaseAudioTagger>() {
      every { name } returns "Mocked M4aAudioTagger"
      every { tag(any(), any()) } returns Result.failure(RuntimeException("Mocked failure"))
    }

    val testSongName = "test song 2"
    assertThatThrownBy {
      tagRunnerCore.doTagging(mockTagger, mockk(), mockk(), 3, SongProperFileName(testSongName))
    }
      .isInstanceOf(RuntimeVocaloidException::class.java)
      .hasMessageContainingIgnoringCase("All extraction attempt on test song 2 by Mocked M4aAudioTagger failed")
  }
}
