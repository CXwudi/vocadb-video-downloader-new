package mikufan.cx.vvd.extractor.config

import jakarta.validation.constraints.Min
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

/**
 * @date 2022-06-12
 * @author CX无敌
 */
@ConfigurationProperties("config.batch")

@Validated
class BatchConfig(
  batchSize: Int,
) {

  @Min(1)
  val batchSize: Int = if (batchSize < 1) Runtime.getRuntime().availableProcessors() else batchSize

  override fun toString(): String {
    return "BatchConfig(batchSize=$batchSize)"
  }
}
