package mikufan.cx.vvd.downloader.config

import mikufan.cx.vocadbapiclient.model.PVServices.Constant.*
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * @date 2021-06-20
 * @author CX无敌
 */
@SpringBootTest
internal class EnablementConfigTest(
  @Autowired val enablement: Enablement
) {

  @Test
  fun `should correctly enable`() {
    log.debug { "enablement = $enablement" }
    assertTrue(enablement.containsKey(NICONICODOUGA))
    assertTrue(enablement.containsKey(YOUTUBE))
    assertTrue(enablement.containsKey(BILIBILI))
  }
}

private val log = KotlinLogging.logger {}
