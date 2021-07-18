package mikufan.cx.vvd.downloader.config

import mikufan.cx.vvd.downloader.config.validation.AreSupportedPvServices
import mikufan.cx.vvd.downloader.util.PVServicesEnum
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty

/**
 * @date 2021-06-18
 * @author CX无敌
 */
@ConfigurationProperties(prefix = "config.preference")
@ConstructorBinding @Validated
data class Preference(
  @field:NotEmpty @field:AreSupportedPvServices val pvPreference: List<PVServicesEnum>,
  @field:Min(0) val maxRetryCount: Int,
  val tryNextPvService: Boolean,
  val tryReprintedPv: Boolean,
  val tryReprintedOnlyAfterOriginalPv: Boolean
)
