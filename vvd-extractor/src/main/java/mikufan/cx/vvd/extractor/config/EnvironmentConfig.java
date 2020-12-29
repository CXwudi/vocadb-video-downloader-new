package mikufan.cx.vvd.extractor.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * @author CX无敌
 * @date 2020-12-28
 */
@ConfigurationProperties(prefix = "config.environment")
@ConstructorBinding
@Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@ToString
public class EnvironmentConfig {

  @NotBlank String pythonLaunchCmd;
  @NotBlank String ffmpegLaunchCmd;

}
