package mikufan.cx.vvd.downloader

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.downloader.config.preference.Preference
import mikufan.cx.vvd.downloader.util.SpringBootTestWithTestProfile
import mikufan.cx.vvd.downloader.util.SpringShouldSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

/**
 * @date 2021-06-18
 * @author CX无敌
 */
@SpringBootTestWithTestProfile
internal class DownloaderApplicationTest(
  @Autowired val ctx: ApplicationContext
) : SpringShouldSpec({

  should("boot") {
    val pvPreference = ctx.getBean(Preference::class.java).pvPreference
    log.debug { "pvPreference = $pvPreference" }
    log.info { "boot success" }
  }
})

private val log = KInlineLogging.logger()
