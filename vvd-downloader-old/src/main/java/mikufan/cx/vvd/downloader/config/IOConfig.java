package mikufan.cx.vvd.downloader.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import mikufan.cx.vvd.common.validation.annotation.IsDirectory;
import mikufan.cx.vvd.common.validation.annotation.PathsNotSame;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * input and output folder
 * @author Charles Chen 101035684
 * @date 2020-12-17
 */
@ConfigurationProperties(prefix = "io")
@ConstructorBinding @Validated
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor @Getter @ToString
@PathsNotSame(fields = {"inputDirectory", "outputDirectory", "errorDirectory"})
public class IOConfig {

  /**
   * can put an custom annotation for checking input dir
   */
  @NotNull @IsDirectory
  Path inputDirectory;

  @NotNull @IsDirectory(optionalCheck = true)
  Path outputDirectory;

  @NotNull @IsDirectory(optionalCheck = true)
  Path errorDirectory;

  @PostConstruct
  private void createOutputDirectories() throws IOException {
    if (Files.notExists(outputDirectory)){
      Files.createDirectories(outputDirectory.toAbsolutePath());
    }

    if (Files.notExists(errorDirectory)){
      Files.createDirectories(errorDirectory.toAbsolutePath());
    }
  }
}
