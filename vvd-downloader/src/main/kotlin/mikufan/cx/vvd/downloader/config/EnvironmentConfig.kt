package mikufan.cx.vvd.downloader.config

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

/**
 * @date 2022-05-14
 * @author CX无敌
 */
@ConfigurationProperties(prefix = "config.environment")

@Validated
data class EnvironmentConfig(
  @field:NotEmpty val mediainfoLaunchCmd: List<@NotBlank String>,
)
