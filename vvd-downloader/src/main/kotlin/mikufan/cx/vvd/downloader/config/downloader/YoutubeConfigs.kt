package mikufan.cx.vvd.downloader.config.downloader

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Conditional
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotEmpty

/**
 * @date 2022-05-21
 * @author CX无敌
 */
@ConfigurationProperties("$YT_CONFIG_PROP_KEY.$YT_YTDL")
@ConstructorBinding
@Validated
@Conditional(YoutubeYtDlCondition::class)
data class YoutubeYtDlConfigs(
  @field:NotEmpty
  override val launchCmd: List<String>,
  val externalArgs: List<String>,
) : DownloaderBaseConfig