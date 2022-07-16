package mikufan.cx.vvd.extractor.component.extractor.base

import mikufan.cx.executil.runCmd
import mikufan.cx.executil.sync
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.commonkt.threading.ExternalProcessThreadFactory
import mikufan.cx.vvd.extractor.config.ProcessConfig
import java.nio.file.Path
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import javax.annotation.PreDestroy
import kotlin.io.path.listDirectoryEntries

/**
 * @date 2022-07-15
 * @author CX无敌
 */
abstract class BaseCliAudioExtractor(
  private val processConfig: ProcessConfig,
) : BaseAudioExtractor() {

  private val processThreadFactory by lazy { ExternalProcessThreadFactory(name) }

  private val threadPool by lazy {
    ThreadPoolExecutor(
      3,
      3,
      processConfig.timeout,
      processConfig.unit,
      LinkedBlockingDeque(),
      processThreadFactory
    )
  }

  /**
   * Extract audio file by executing a command line
   */
  override fun tryExtract(inputPvFile: Path, baseOutputFileName: String, outputDirectory: Path): Path {
    // build commands
    val commands = buildCommands(inputPvFile, baseOutputFileName, outputDirectory)

    // execute commands
    log.info { "Executing commands: ${commands.joinToString(" ", "`", "`")}" }
    executeCommands(commands)
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
  abstract fun buildCommands(inputPvFile: Path, baseOutputFileName: String, outputDirectory: Path): List<String>

  protected open fun executeCommands(commands: List<String>) {
    runCmd(*commands.toTypedArray()).sync(processConfig.timeout, processConfig.unit, threadPool) {
      // the order must be stdout first and stderr second, due to how ExternalProcessThreadFactory is coded
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
    processThreadFactory.resetCounter()
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

  @PreDestroy
  fun shutdownThreadPool() {
    log.debug { "Shutting down the common pool in $name" }
    threadPool.shutdown()
  }
}

private val log = KInlineLogging.logger()
