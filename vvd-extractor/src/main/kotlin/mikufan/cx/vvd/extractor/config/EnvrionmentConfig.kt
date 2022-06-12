package mikufan.cx.vvd.extractor.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

/**
 * @date 2022-06-12
 * @author CX无敌
 */
@ConfigurationProperties("config.environment")
@Validated
@ConstructorBinding
data class EnvrionmentConfig(
  @field:NotEmpty val pythonLaunchCmd: List<@NotBlank String>,
  @field:NotEmpty val ffmpegLaunchCmd: List<@NotBlank String>,
  @field:NotEmpty val mediainfoLaunchCmd: List<@NotBlank String>,
)
