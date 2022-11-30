package mikufan.cx.vvd.downloader.config.downloader

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Conditional
import org.springframework.validation.annotation.Validated

/**
 * @date 2022-05-21
 * @author CX无敌
 */
@ConfigurationProperties("$YT_CONFIG_PROP_KEY.$YT_YTDL")

@Validated
@Conditional(YoutubeYtDlCondition::class)
data class YoutubeYtDlConfigs(
  @field:NotEmpty
  override val launchCmd: List<@NotBlank String>,
  val externalArgs: List<@NotBlank String>,
) : DownloaderBaseConfig