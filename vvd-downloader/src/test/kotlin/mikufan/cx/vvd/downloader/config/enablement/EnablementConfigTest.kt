package mikufan.cx.vvd.downloader.config.enablement

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.commonkt.vocadb.api.model.PVService
import mikufan.cx.vvd.downloader.util.SpringBootTestWithTestProfile
import org.assertj.core.api.Assertions.assertThat
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
internal class EnablementConfig2Test(
  @Autowired val enablement: Enablement
) {
  @Test
  fun correctlyEnable() {
    log.debug { "enablement = $enablement" }
    // should see that only youtube's and niconico's config existence are checked
    assertThat(enablement[PVService.NICONICODOUGA])
      .containsExactlyInAnyOrder("youtube-dl", "other-downloader")
  }
}

private val log = KInlineLogging.logger()
