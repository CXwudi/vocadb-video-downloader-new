package mikufan.cx.vvd.extractor.service

import io.kotest.assertions.throwables.shouldNotThrow
import mikufan.cx.vvd.extractor.util.SpringBootDirtyTestWithTestProfile
import mikufan.cx.vvd.extractor.util.SpringShouldSpec

/**
 * only this vvd-extractor module can have this integration test running in CI without fare
 *
 * if running in local environment, be aware that it will move files, and don't commit that changes
 */
@SpringBootDirtyTestWithTestProfile(
  customProperties = [
  ]
)
class MainServiceTest(
  private val mainService: MainService
) : SpringShouldSpec({
  xshould("run on this test env") {
    shouldNotThrow<Exception> {
      mainService.run()
    }
  }
})
