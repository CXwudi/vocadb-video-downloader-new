package mikufan.cx.vvd.downloader.component.downloader.base

import tools.jackson.databind.ObjectMapper
import mikufan.cx.vvd.downloader.config.DownloadConfig
import mikufan.cx.vvd.downloader.config.EnvironmentConfig
import org.apache.tika.Tika
import java.nio.file.Path

/**
 * A common base downloader for all youtube-dl and it's variants
 *
 * The youtube-dl executable must make sure the -o and --write-thumbnail options are always available.
 * For -o option, `%(ext)s` is always available
 *
 * @date 2022-05-21
 * @author CX无敌
 */
abstract class BaseYtDlDownloader(
  downloadConfig: DownloadConfig,
  tika: Tika,
  environmentConfig: EnvironmentConfig,
  objectMapper: ObjectMapper
) : BaseCliDownloader(
  downloadConfig, tika, environmentConfig, objectMapper
) {

  abstract val launchCmd: List<String>
  abstract val externalArgs: List<String>

  /**
   * Build the command line used to download the needed resources (pv/audio + thumbnail).
   *
   * The command line will be run under the current directory of this project (so that user can specify the relative path of the executable file).
   *
   * The downloaded files must be stored in [outputDirectory]
   *
   * The downloaded files must contain the string [baseFileName] in their file names (so that we can find these downloaded files).
   *
   * @param baseFileName the base file name of the downloaded files
   * @param outputDirectory the directory where the downloaded files will be stored
   * @param url the url of the pv to be downloaded
   * @return the command line in list of strings that will be used to download the needed resources
   */
  override fun buildCommand(url: String, baseFileName: String, outputDirectory: Path): List<String> {
    return buildList {
      addAll(launchCmd)
      addAll(externalArgs)
      add("--write-thumbnail")
      addAll(
        listOf(
          url,
          "-o",
          "${outputDirectory.toAbsolutePath()}/$baseFileName.%(ext)s",
        )
      )
    }
  }
}
