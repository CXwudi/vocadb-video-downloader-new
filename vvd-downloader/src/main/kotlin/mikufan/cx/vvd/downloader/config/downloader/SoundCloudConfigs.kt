package mikufan.cx.vvd.downloader.config.downloader

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Conditional
import org.springframework.validation.annotation.Validated

@ConfigurationProperties("$SC_CONFIG_PROP_KEY.$SC_YTDL")
@Validated
@Conditional(SoundCloudYtDlCondition::class)
data class SoundCloudYtDlConfig(
  @field:NotEmpty
  override val launchCmd: List<@NotBlank String>,
  val externalArgs: List<@NotBlank String>,
) : DownloaderBaseConfig