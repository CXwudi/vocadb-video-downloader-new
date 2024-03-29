package mikufan.cx.vvd.downloader.config.enablement

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.commonkt.vocadb.PVServicesEnum
import mikufan.cx.vvd.downloader.util.SpringBootTestWithTestProfile
import mikufan.cx.vvd.downloader.util.SpringShouldSpec
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
) : SpringShouldSpec({
  
  should("correctly enable") {
    log.debug { "enablement = $enablement" }
    // should see that only youtube's and niconico's config existence are checked
    enablement[PVServicesEnum.NICONICODOUGA] shouldContainExactlyInAnyOrder listOf("youtube-dl", "other-downloader")
  }
})

private val log = KInlineLogging.logger()
