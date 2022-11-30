package mikufan.cx.vvd.downloader.config

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated
import java.util.concurrent.TimeUnit

/**
 * Some configuration during downloading
 * @date 2022-05-14
 * @author CX无敌
 */
@ConfigurationProperties(prefix = "config.download")

@Validated
data class DownloadConfig(
  /**
   * Maximum time to wait for downloading PV and thumbnail
   */
  @field:Positive val timeout: Long,
  /**
   * Time unit of timeout
   */
  // currently https://hibernate.atlassian.net/browse/HV-1852 mentioned a false warning bug of HV000254
  @field:NotNull val unit: TimeUnit,
)
