package mikufan.cx.vvd.downloader.config.downloader

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.downloader.util.SpringBootTestWithTestProfile
import mikufan.cx.vvd.downloader.util.SpringShouldSpec
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory

/**
 * @date 2021-06-27
 * @author CX无敌
 */
@SpringBootTestWithTestProfile
internal class NicoNicoYtDlConfigTest(
  // the niconico config bean should exist
  @Autowired val nicoNicoYtDlConfig: NicoNicoYtDlConfig
) : SpringShouldSpec({

  should("exist") {
    log.debug { "nicoNicoYtDlConfig = $nicoNicoYtDlConfig" }
    shouldBe(true) // if this is reached, then the niconico config bean condition is correct
  }
})

private val log = KInlineLogging.logger()

@SpringBootTestWithTestProfile(
  customProperties = [
    "config.preference.pv-preference=Youtube, Bilibili" // no niconico, should skipped niconico config
  ]
)
internal class NicoNicoYtDlConfigConditionalSkippedTest(
  @Autowired val beanFactory: ConfigurableBeanFactory
) : SpringShouldSpec({

  should("not exist") {
    shouldThrow<NoSuchBeanDefinitionException> { beanFactory.getBean(NicoNicoYtDlConfig::class.java) }
  }
})
