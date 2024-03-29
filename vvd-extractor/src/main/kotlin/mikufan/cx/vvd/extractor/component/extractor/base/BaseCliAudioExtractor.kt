package mikufan.cx.vvd.extractor.component.extractor.base

import mikufan.cx.executil.runCmd
import mikufan.cx.executil.sync
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.extractor.config.ProcessConfig
import org.springframework.beans.factory.annotation.Qualifier
import java.nio.file.Path
import java.util.concurrent.ThreadPoolExecutor
import kotlin.io.path.listDirectoryEntries

/**
 * @date 2022-07-15
 * @author CX无敌
 */
abstract class BaseCliAudioExtractor(
  protected val processConfig: ProcessConfig,
  @Qualifier("extractorThreadPool") protected val threadPool: ThreadPoolExecutor
) : BaseAudioExtractor() {

  /**
   * Extract audio file by executing a command line
   */
  override fun tryExtract(inputPvFile: Path, baseOutputFileName: String, outputDirectory: Path): Path {
    // build commands
    val command = buildCommand(inputPvFile, baseOutputFileName, outputDirectory)

    // execute commands
    log.info { "Executing commands: ${command.joinToString(" ", "`", "`")}" }
    executeCommand(command)
    log.info { "Done command execution for $baseOutputFileName, start finding the extracted audio file" }
    // find the extracted audio file
    return findExtractedAudioFile(outputDirectory, baseOutputFileName)
  }

  /**
   * Build the commands to extract the audio track from the PV file of the song to the [outputDirectory] with a base file name [baseOutputFileName].
   *
   * The command line will be run under the current directory of this project (so that user can specify the relative path of the executable file).
   *
   * The output audio file must be stored in the [outputDirectory]. And the file name must contain [baseOutputFileName].
   *
   * @param inputPvFile Path the PV file of the song.
   * @param baseOutputFileName String the base file name of the output audio file.
   * @param outputDirectory Path the directory to store the output audio file.
   * @return List<String> the commands to be executed.
   */
  abstract fun buildCommand(inputPvFile: Path, baseOutputFileName: String, outputDirectory: Path): List<String>

  protected open fun executeCommand(commands: List<String>) {
    val process = runCmd(commands)
    process.sync(processConfig.timeout, processConfig.unit, threadPool) {
      onStdOutEachLine {
        if (it.isNotBlank()) {
          log.info { it }
        }
      }
      onStdErrEachLine {
        if (it.isNotBlank()) {
          log.debug { it }
        }
      }
    }
    process.exitValue().let {
      if (it != 0) {
        throw IllegalStateException("Command failed with exit code $it")
      }
    }
  }

  /**
   * It is encouraged to override this method to find the extracted audio file,
   * instead of relying on [Path.listDirectoryEntries] to find the extracted audio file.
   * @param outputDirectory Path the directory to store the output audio file.
   * @param baseOutputFileName String the base file name of the output audio file.
   * @return Path the path of the extracted audio file.
   */
  protected open fun findExtractedAudioFile(outputDirectory: Path, baseOutputFileName: String): Path {
    val possibleFiles =
      outputDirectory.listDirectoryEntries("*${baseOutputFileName.replace("[", "\\[").replace("]", "\\]")}*")
    if (possibleFiles.isEmpty()) {
      throw IllegalStateException("No extracted audio file that contain $baseOutputFileName as file name found in $outputDirectory")
    } else if (possibleFiles.size >= 2) {
      throw IllegalStateException("This should not happened, more than one file found contains $baseOutputFileName.")
    } else {
      return possibleFiles.first()
    }
  }
}

private val log = KInlineLogging.logger()
