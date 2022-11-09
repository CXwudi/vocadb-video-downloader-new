package mikufan.cx.vvd.extractor.component

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import mikufan.cx.vvd.extractor.component.tagger.base.BaseAudioTagger
import mikufan.cx.vvd.extractor.util.SpringBootDirtyTestWithTestProfile
import mikufan.cx.vvd.extractor.util.SpringShouldSpec
import org.springframework.beans.factory.annotation.Qualifier

@SpringBootDirtyTestWithTestProfile
class TagRunnerCoreTest(
  private val tagRunnerCore: TagRunnerCore,
  // an working example to inject a mock bean with qualifier
//  @field:MockkBean(name = "m4aAudioTagger") // this let mockk-spring framework knows the bean
//  @Qualifier("m4aAudioTagger") // this let spring framework know the bean
//  private val mockTagger: BaseAudioTagger,// because we want to access the bean, we have to declare it on constructor,
) : SpringShouldSpec({

  val mockTagger = mockk<BaseAudioTagger>() {
    every { name } returns "Mocked M4aAudioTagger"
  }

  context("tag runner core") {
    should("success when tagger success") {
      mockTagger.apply {
        every { tag(any(), any()) } returns Result.success(Unit)
      }
      // TODO()
    }

    should("throw when all attempts failed") {
      mockTagger.apply {
        every { tag(any(), any()) } returns Result.failure(RuntimeException("Mocked failure"))
      }
      // TODO()
    }
  }
})
