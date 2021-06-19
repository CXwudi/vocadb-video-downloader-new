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
  val inputListId: Int,
  // in Kotlin, annotations here by default are constructor's parameters. https://kotlinlang.org/docs/annotations.html#annotation-use-site-targets
  // however, spring doesn't support constructor validation. https://www.baeldung.com/javax-validation-method-constraints#1-automatic-validation-with-spring
  // so we need to make it either field or getter annotations by @field:Annotation or @get:Annotation
  // if @get:Annotation, spring will still evaluate it before application is up
  @field:IsDirectory(optionalCheck = true) val outputDirectory: Path,
  @field:IsDirectory(optionalCheck = true) val errorDirectory: Path) {

  init {
    if (Files.notExists(outputDirectory)) {
      Files.createDirectories(outputDirectory)
    }
    if (Files.notExists(errorDirectory)) {
      Files.createDirectories(errorDirectory)
    }
  }
}
