package mikufan.cx.vvd.extractor.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.InstantSource
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * @author CX无敌
 * 2022-11-24
 */
@Configuration
class InstantSourceConfig {

  @Bean
  fun instantSource(currentTimeConfig: CurrentTimeConfig): InstantSource {
    val startFrom = currentTimeConfig.startFrom
    return if (startFrom.isNullOrBlank()) {
      configRealInstantSource()
    } else {
      configFixedInstantSource(startFrom)
    }
  }

  private fun configRealInstantSource() = InstantSource.system()

  private fun configFixedInstantSource(startFrom: String): InstantSource {
    val localDateTime = LocalDateTime.parse(startFrom, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    val instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant()
    return InstantSource { instant }
  }
}

@ConfigurationProperties(prefix = "config.current-time")
@ConstructorBinding
data class CurrentTimeConfig(
  val startFrom: String? = null,
)
