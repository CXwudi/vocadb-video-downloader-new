package mikufan.cx.vvd.extractor.config

import jakarta.validation.constraints.PositiveOrZero
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

/**
 * @date 2022-07-19
 * @author CX无敌
 */
@ConfigurationProperties(prefix = "config.retry")
@Validated
data class RetryPreference(
  @field:PositiveOrZero val retryOnExtraction: Int,
  @field:PositiveOrZero val retryOnTagging: Int,
)
