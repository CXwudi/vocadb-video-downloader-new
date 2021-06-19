package mikufan.cx.vvd.downloader.config

import mikufan.cx.vvd.common.validation.annotation.IsDirectory
import mikufan.cx.vvd.common.validation.annotation.PathsNotSame
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import java.nio.file.Files
import java.nio.file.Path

/**
 * @date 2021-06-18
 * @author CX无敌
 */
@ConfigurationProperties(prefix = "io")
@ConstructorBinding
@Validated
@PathsNotSame(fields = ["inputDirectory", "outputDirectory", "errorDirectory"])
data class IOConfig(
  @field:IsDirectory val inputDirectory: Path,
  @field:IsDirectory(optionalCheck = true) val outputDirectory: Path,
  @field:IsDirectory(optionalCheck = true) val errorDirectory: Path
) {

  init {
    if (Files.notExists(outputDirectory)) {
      Files.createDirectories(outputDirectory)
    }
    if (Files.notExists(errorDirectory)) {
      Files.createDirectories(errorDirectory)
    }
  }
}
