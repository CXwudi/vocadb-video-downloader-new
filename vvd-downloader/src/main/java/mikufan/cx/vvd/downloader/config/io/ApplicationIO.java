package mikufan.cx.vvd.downloader.config.io;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import mikufan.cx.vvd.common.validation.annotation.IsDirectory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.nio.file.Path;

/**
 * @author Charles Chen 101035684
 * @date 2020-12-17
 */
@ConfigurationProperties(prefix = "downloader.io")
@ConstructorBinding @Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor @Getter @ToString
public class ApplicationIO {

  /**
   * can put an custom annotation for checking input dir
   */
  @NotNull @IsDirectory
  Path inputDirectory;

  @NotNull
  Path outputDirectory;
}
