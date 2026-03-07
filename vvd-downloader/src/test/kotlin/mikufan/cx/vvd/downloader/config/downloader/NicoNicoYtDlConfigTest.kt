package mikufan.cx.vvd.downloader.config.downloader

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.downloader.util.SpringBootTestWithTestProfile
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue
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
  @param:Autowired val nicoNicoYtDlConfig: NicoNicoYtDlConfig
) {
  @Test
  fun exist() {
    log.debug { "nicoNicoYtDlConfig = $nicoNicoYtDlConfig" }
    assertTrue(true) // if this is reached, then the niconico config bean condition is correct
  }
}

private val log = KInlineLogging.logger()

@SpringBootTestWithTestProfile(
  customProperties = [
    "config.preference.pv-preference=Youtube, Bilibili" // no niconico, should skipped niconico config
  ]
)
internal class NicoNicoYtDlConfigConditionalSkippedTest(
  @param:Autowired val beanFactory: ConfigurableBeanFactory
) {
  @Test
  fun notExist() {
    assertThatThrownBy { beanFactory.getBean(NicoNicoYtDlConfig::class.java) }
      .isInstanceOf(NoSuchBeanDefinitionException::class.java)
  }
}
