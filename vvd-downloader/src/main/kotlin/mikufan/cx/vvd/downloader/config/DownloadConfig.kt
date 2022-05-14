package mikufan.cx.vvd.downloader.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import java.util.concurrent.TimeUnit
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

/**
 * Some configuration during downloading
 * @date 2022-05-14
 * @author CX无敌
 */
@ConfigurationProperties(prefix = "config.download")
@ConstructorBinding
@Validated
data class DownloadConfig(
  @field:Positive val timeout: Long,
  @field:NotNull val unit: TimeUnit,
)
