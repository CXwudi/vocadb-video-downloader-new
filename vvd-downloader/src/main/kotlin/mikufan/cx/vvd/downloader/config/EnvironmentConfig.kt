package mikufan.cx.vvd.downloader.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

/**
 * @date 2022-05-14
 * @author CX无敌
 */
@ConfigurationProperties(prefix = "config.environment")
@ConstructorBinding
@Validated
data class EnvironmentConfig(
  @field:NotEmpty val mediainfoLaunchCmd: List<@NotBlank String>,
)
