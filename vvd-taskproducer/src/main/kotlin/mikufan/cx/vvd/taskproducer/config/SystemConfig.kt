package mikufan.cx.vvd.taskproducer.config

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Range
import org.hibernate.validator.constraints.URL
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

/**
 * @date 2021-06-12
 * @author CX无敌
 */
@ConfigurationProperties(prefix = "config")

@Validated
class SystemConfig(
  @field:URL val baseUrl: String,
  @field:NotBlank val userAgent: String,
  @field:Range(min = 1, max = 50) val apiPageSize: Int,
  batchSize: Int
) {
  @Min(1)
  val batchSize = if (batchSize < 1) Runtime.getRuntime().availableProcessors() else batchSize

  override fun toString(): String {
    return "SystemConfig(baseUrl='$baseUrl', userAgent='$userAgent', apiPageSize=$apiPageSize, batchSize=$batchSize)"
  }
}
