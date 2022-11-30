package mikufan.cx.vvd.downloader.config.preference

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import mikufan.cx.vvd.commonkt.vocadb.PVServicesEnum
import mikufan.cx.vvd.downloader.config.validation.AreSupportedPvServices
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

/**
 * @date 2021-06-18
 * @author CX无敌
 */
@ConfigurationProperties(prefix = "config.preference")

@Validated
data class Preference(
  @field:NotEmpty @field:AreSupportedPvServices val pvPreference: List<PVServicesEnum>,
  @field:Min(0) val maxRetryCount: Int,
  val tryNextPvServiceOnFail: Boolean,
  val tryReprintedPv: Boolean,
  val tryAllOriginalPvsBeforeReprintedPvs: Boolean
)
