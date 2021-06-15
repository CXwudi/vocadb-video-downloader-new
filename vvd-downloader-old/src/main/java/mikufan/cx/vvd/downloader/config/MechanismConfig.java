package mikufan.cx.vvd.downloader.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

/**
 * setting about downloading behavior
 * @author CX无敌
 * @date 2020-12-25
 */
@ConfigurationProperties(prefix = "config.mechanism")
@ConstructorBinding
@Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@ToString
public class MechanismConfig {

  /**
   * how many time can we retry if download fail
   */
  @Min(0)
  int maxAllowedRetryCount;
}
