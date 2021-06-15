package mikufan.cx.vvd.downloader.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import mikufan.cx.vvd.common.validation.annotation.IsSupportedPvServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author CX无敌
 * @date 2020-12-21
 */
@ConfigurationProperties(prefix = "config.pv-preference")
@ConstructorBinding
@Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@ToString
public class PvPreferenceConfig {
  @NotEmpty @IsSupportedPvServices
  List<String> preference;
}
