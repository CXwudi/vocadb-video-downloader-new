package mikufan.cx.vvd.extractor

import mikufan.cx.vvd.extractor.util.SpringShouldSpec
import mikufan.cx.vvd.extractor.util.SpringBootDirtyTestWithTestProfile
import io.kotest.matchers.shouldBe
import mikufan.cx.vvd.extractor.util.SpringBootTestWithTestProfile

@SpringBootTestWithTestProfile
class ExtractorApplicationTest : SpringShouldSpec({
  context("extractor applition") {
    should("boot") {

    }
  }
})
