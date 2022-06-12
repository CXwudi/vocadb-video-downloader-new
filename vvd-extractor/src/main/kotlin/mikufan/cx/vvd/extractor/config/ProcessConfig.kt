package mikufan.cx.vvd.extractor.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import java.util.concurrent.TimeUnit
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

/**
 * @date 2022-06-12
 * @author CX无敌
 */
@ConfigurationProperties("config.process")
@ConstructorBinding
@Validated
data class ProcessConfig(
  @field:Positive val timeout: Long,
  @field:NotNull val unit: TimeUnit
)
