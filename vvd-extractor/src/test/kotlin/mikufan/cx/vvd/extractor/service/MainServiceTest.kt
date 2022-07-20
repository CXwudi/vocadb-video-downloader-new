package mikufan.cx.vvd.extractor.service

import io.kotest.assertions.throwables.shouldNotThrow
import mikufan.cx.vvd.extractor.util.SpringBootDirtyTestWithTestProfile
import mikufan.cx.vvd.extractor.util.SpringShouldSpec

@SpringBootDirtyTestWithTestProfile(
  customProperties = [

  ]
)
class MainServiceTest(
  private val mainService: MainService
) : SpringShouldSpec({
  should("run on this test env") {
    shouldNotThrow<Exception> {
      mainService.run()
    }
  }
})
