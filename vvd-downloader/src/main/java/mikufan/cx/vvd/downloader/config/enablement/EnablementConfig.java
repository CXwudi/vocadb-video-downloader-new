package mikufan.cx.vvd.downloader.config.enablement;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import java.util.Map;
import java.util.Optional;

/**
 * @author CX无敌
 * @date 2020-12-21
 */
@ConfigurationProperties(prefix = "downloader.config.enablement")
@ConstructorBinding
@Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@ToString
public class EnablementConfig {

  Map<String, String> map;

  public Optional<String> getEnablementForService(String pvService){
    return Optional.ofNullable(map.get(pvService));
  }
}
