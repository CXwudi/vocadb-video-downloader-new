package mikufan.cx.vvd.extractor.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.PositiveOrZero

/**
 * @date 2022-07-19
 * @author CX无敌
 */
@ConfigurationProperties(prefix = "config.retry")
@ConstructorBinding
@Validated
data class RetryPreference(
  @field:PositiveOrZero val retryOnExtraction: Int,
  @field:PositiveOrZero val retryOnTagging: Int,
)
