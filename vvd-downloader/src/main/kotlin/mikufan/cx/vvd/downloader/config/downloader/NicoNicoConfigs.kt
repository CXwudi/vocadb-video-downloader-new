package mikufan.cx.vvd.downloader.config.downloader

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotEmpty

/**
 * Files of all niconico download config classes
 * @date 2021-06-27
 * @author CX无敌
 */

const val NICONICO_CONFIG_KEY = "config.downloader.NicoNicoDouga"
const val NICONICO_CONFIG_PROP_KEY = "config.downloader.nico-nico-douga"
const val YOUTUBE_DL = "youtube-dl"
const val NN_DOWNLOAD = "nndownload"


@ConfigurationProperties("$NICONICO_CONFIG_PROP_KEY.$YOUTUBE_DL") // warn: you have to change NicoNicoDouga to nico-nico-douga
@ConstructorBinding @Validated
//TODO: add conditional
data class NicoNicoYtDlConfig(
  @field:NotEmpty
  override val launchCmd: List<String>,
  val externalArgs: List<String>,
) : DownloaderBaseConfig