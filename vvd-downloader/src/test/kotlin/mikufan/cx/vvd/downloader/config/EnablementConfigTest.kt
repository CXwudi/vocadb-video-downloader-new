package mikufan.cx.vvd.downloader.config

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.downloader.util.PVServicesEnum
import mikufan.cx.vvd.downloader.util.SpringBootTestWithTestProfile
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

/**
 * @date 2021-06-20
 * @author CX无敌
 */
@SpringBootTestWithTestProfile(
  customProperties = [
    "config.preference.pv-preference=NicoNicoDouga, Youtube",
    "config.enablement.NicoNicoDouga=youtube-dl, other-downloader",
    "config.downloader.NicoNicoDouga.other-downloader.launch-cmd=./command,--some-args",
  ]
)
internal class EnablementConfigTest(
  @Autowired val enablement: Enablement
) {

  @Test
  fun `should correctly enable`() {
    log.debug { "enablement = $enablement" }
    // should see that only youtube's and niconico's config existence are checked
    assertThat(enablement[PVServicesEnum.NICONICODOUGA], containsInAnyOrder("youtube-dl", "other-downloader"))
  }
}

private val log = KInlineLogging.logger()
