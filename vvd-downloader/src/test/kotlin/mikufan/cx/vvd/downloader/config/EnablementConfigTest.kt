package mikufan.cx.vvd.downloader.config

import mikufan.cx.vocadbapiclient.model.PVServices.Constant.*
import mikufan.cx.vvd.downloader.util.SpringBootTestWithTestProfile
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

/**
 * @date 2021-06-20
 * @author CX无敌
 */
@SpringBootTestWithTestProfile
internal class EnablementConfigTest(
  @Autowired val enablement: Enablement
) {

  @Test
  fun `should correctly enable`() {
    log.debug { "enablement = $enablement" }
    assertTrue(enablement.containsPvService(NICONICODOUGA))
    assertTrue(enablement.containsPvService(YOUTUBE))
    assertTrue(enablement.containsPvService(BILIBILI))
  }
}

private val log = KotlinLogging.logger {}