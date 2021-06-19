package mikufan.cx.vvd.taskproducer.config

import org.hibernate.validator.constraints.Range
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

/**
 * @date 2021-06-12
 * @author CX无敌
 */
@ConfigurationProperties(prefix = "config")
@ConstructorBinding @Validated
data class SystemConfig(
  @field:NotBlank val baseUrl: String,
  @field:NotBlank val userAgent: String,
  @field:Range(min = 1, max = 50) val apiPageSize: Int,
  @field:Min(1) val batchSize: Int
)