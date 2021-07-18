package mikufan.cx.vvd.downloader.config.downloader

import mikufan.cx.vvd.downloader.util.SpringBootTestWithTestProfile
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

/**
 * @date 2021-06-27
 * @author CX无敌
 */
@SpringBootTestWithTestProfile
internal class NicoNicoYtDlConfigTest(
  @Autowired val nicoNicoYtDlConfig: NicoNicoYtDlConfig
) {

  @Test
  fun `should exist`() {
    log.debug { "nicoNicoYtDlConfig = $nicoNicoYtDlConfig" }
    assertTrue(true) // if this is reached, then the niconico config bean condition is correct
  }
}

private val log = KotlinLogging.logger {}
