package mikufan.cx.vvd.extractor.service

import mikufan.cx.vvd.extractor.util.SpringBootTestWithTestProfile
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

/**
 * only this vvd-extractor module can have this integration test running in CI without fare
 *
 * if running in local environment, be aware that it will move files, and don't commit that changes
 */
@SpringBootTestWithTestProfile(
  customProperties = [
  ]
)
class MainServiceTest(
  private val mainService: MainService
) {
  @Disabled("run in proper env only")
  @Test
  fun runOnTestEnv() {
    assertDoesNotThrow { mainService.run() }
  }
}
