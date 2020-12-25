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
 * @author CX无敌
 * @date 2020-12-20
 */
@ConfigurationProperties(prefix = "config.youtube")
@ConstructorBinding
@Validated
@Getter
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class YoutubeYoutubeDlConfig {

  @NotNull @IsFile
  Path youtubeDlPath;

  @NotNull @IsFile
  Path ffmpegPath;

  Map<String, String> youtubeDlOptions;

  public YoutubeYoutubeDlConfig(@NotNull Path youtubeDlPath, Path ffmpegPath, Map<String, String> youtubeDlOptions) {
    this.youtubeDlPath = youtubeDlPath;
    this.ffmpegPath = ffmpegPath;
    this.youtubeDlOptions = ConfigHelper.fixConfigWithDash(youtubeDlOptions);
  }
}
