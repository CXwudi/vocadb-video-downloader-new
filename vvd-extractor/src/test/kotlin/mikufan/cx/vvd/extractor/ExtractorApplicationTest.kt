package mikufan.cx.vvd.extractor

import mikufan.cx.vvd.extractor.util.SpringShouldSpec
import mikufan.cx.vvd.extractor.util.SpringBootDirtyTestWithTestProfile
import io.kotest.matchers.shouldBe

@SpringBootDirtyTestWithTestProfile
class ExtractorApplicationTest : SpringShouldSpec({
  context("extractor applition") {
    should("boot") {

    }
  }
})
