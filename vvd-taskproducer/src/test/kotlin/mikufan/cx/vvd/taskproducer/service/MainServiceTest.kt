package mikufan.cx.vvd.taskproducer.service

import mikufan.cx.inlinelogging.KInlineLogging
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * @date 2021-09-28
 * @author CX无敌
 */
@SpringBootTest(
  properties = [
    "io.input-list-id=9909", // 11058 = 2022年V家新曲, 10018 = 2021年V家新曲, 10020 = 2021年V家良曲, 9909 = Hatsune Miku Magical Mirai 2021
    "config.batch-size=1",
  ]
)
@Disabled // just to make sure main service runs on real production
internal class MainServiceTest(
  @Autowired private val mainService: MainService
) {

  @Test
  fun `should works`() {
    log.info { "main server boots" }
    mainService.run()
  }
}

private val log = KInlineLogging.logger()
