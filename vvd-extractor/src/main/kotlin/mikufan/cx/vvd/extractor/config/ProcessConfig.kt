package mikufan.cx.vvd.extractor.config

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated
import java.util.concurrent.TimeUnit

/**
 * @date 2022-06-12
 * @author CX无敌
 */
@ConfigurationProperties("config.process")

@Validated
data class ProcessConfig(
  @field:Positive val timeout: Long,
  @field:NotNull val unit: TimeUnit
)
