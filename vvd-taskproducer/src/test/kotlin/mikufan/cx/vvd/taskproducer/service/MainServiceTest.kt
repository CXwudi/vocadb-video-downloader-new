package mikufan.cx.vvd.taskproducer.service

import mikufan.cx.inlinelogging.KInlineLogging
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * @date 2021-09-28
 * @author CX无敌
 */
@SpringBootTest(
  properties = ["io.input-list-id=9197"]
)
// @Disabled // just to make sure main service runs on real production
internal class MainService2Test(
  @Autowired private val mainService: MainService
) {

  @Test
  fun `should works`() {
    log.info { "main server boots" }
    mainService.run()
  }
}

private val log = KInlineLogging.logger()
