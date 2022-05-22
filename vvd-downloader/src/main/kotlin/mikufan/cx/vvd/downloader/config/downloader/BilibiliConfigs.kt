package mikufan.cx.vvd.downloader.config.downloader

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Conditional
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

/**
 * @date 2022-05-21
 * @author CX无敌
 */
@ConfigurationProperties("$BILI_CONFIG_PROP_KEY.$BILI_YTDL")
@ConstructorBinding
@Validated
@Conditional(BilibiliYtDlCondition::class)
data class BilibiliYtDlConfigs(
  @field:NotEmpty
  override val launchCmd: List<@NotBlank String>,
  val externalArgs: List<@NotBlank String>,
) : DownloaderBaseConfig