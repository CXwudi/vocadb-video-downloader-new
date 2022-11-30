package mikufan.cx.vvd.downloader.config.downloader

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Conditional
import org.springframework.validation.annotation.Validated

/**
 * Files of all niconico download config classes
 * @date 2021-06-27
 * @author CX无敌
 */
@ConfigurationProperties("$NND_CONFIG_PROP_KEY.$NND_YTDL")

@Validated
@Conditional(NicoNicoYtDlCondition::class)
data class NicoNicoYtDlConfig(
  @field:NotEmpty
  override val launchCmd: List<@NotBlank String>,
  val externalArgs: List<@NotBlank String>,
) : DownloaderBaseConfig
