package mikufan.cx.vvd.downloader.config.downloader;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import mikufan.cx.vvd.common.validation.annotation.IsFile;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.util.Map;

/**
 * @author Charles Chen 101035684
 * @date 2020-12-18
 */
@ConfigurationProperties(prefix = "config.downloader.nico-unsafe-idm-youtube-dl")
@ConstructorBinding
@Validated
@Getter
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class NicoUnsafeIdmYoutubeDlConfig {

  @NotNull @IsFile
  Path youtubeDlPath;

  @NotNull @IsFile
  Path idmPath;

  Map<String, String> youtubeDlOptions;

  public NicoUnsafeIdmYoutubeDlConfig(Path youtubeDlPath, Path idmPath, Map<String, String> youtubeDlOptions) {
    this.youtubeDlPath = youtubeDlPath;
    this.idmPath = idmPath;
    // in spring boot, key start with dash is removed, add them back
    this.youtubeDlOptions = ConfigHelper.fixConfigWithDash(youtubeDlOptions);

  }
}
