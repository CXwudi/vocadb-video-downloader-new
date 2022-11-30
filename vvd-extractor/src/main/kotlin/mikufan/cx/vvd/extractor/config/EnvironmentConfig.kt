package mikufan.cx.vvd.extractor.config

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

/**
 * @date 2022-06-12
 * @author CX无敌
 */
@ConfigurationProperties("config.environment")
@Validated
data class EnvironmentConfig(
  @field:NotEmpty val pythonLaunchCmd: List<@NotBlank String>,
  @field:NotEmpty val ffmpegLaunchCmd: List<@NotBlank String>,
  @field:NotEmpty val mediainfoLaunchCmd: List<@NotBlank String>,
)
