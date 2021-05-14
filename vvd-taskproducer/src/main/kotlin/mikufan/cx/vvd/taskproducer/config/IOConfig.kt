package mikufan.cx.vvd.taskproducer.config

import mikufan.cx.vvd.common.validation.annotation.IsDirectory
import mikufan.cx.vvd.common.validation.annotation.PathsNotSame
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import java.nio.file.Files
import java.nio.file.Path

/**
 * @date 2021-05-13
 * @author CX无敌
 */
@ConfigurationProperties(prefix = "io")
@ConstructorBinding @Validated
@PathsNotSame(fields = ["outputDirectory", "errorDirectory"])
data class IOConfig(
  val inputListId: Integer,
  @IsDirectory(optionalCheck = true) val outputDirectory: Path,
  @IsDirectory(optionalCheck = true) val errorDirectory: Path){

  init {
    if (Files.notExists(outputDirectory)){
      Files.createDirectories(outputDirectory)
    }
    if (Files.notExists(errorDirectory)){
      Files.createDirectories(errorDirectory)
    }
  }

}
