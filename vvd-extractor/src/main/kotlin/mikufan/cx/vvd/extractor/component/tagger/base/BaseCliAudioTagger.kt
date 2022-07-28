package mikufan.cx.vvd.extractor.component.tagger.base

import mikufan.cx.executil.runCmd
import mikufan.cx.executil.sync
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.extractor.config.ProcessConfig
import mikufan.cx.vvd.extractor.model.VSongTask
import org.springframework.beans.factory.annotation.Qualifier
import java.nio.file.Path
import java.util.concurrent.ThreadPoolExecutor

/**
 * Base audio tagger that runs a command line.
 * @date 2022-07-26
 * @author CX无敌
 */
abstract class BaseCliAudioTagger(
  private val processConfig: ProcessConfig,
  @Qualifier("taggerThreadPool") private val threadPool: ThreadPoolExecutor
) : BaseAudioTagger() {

  override fun tryTag(audioFile: Path, allInfo: VSongTask) {
    val command = buildCommand(audioFile, allInfo)
    log.info { "Executing commands: ${command.joinToString(" ", "`", "`")}" }
    executeCommand(command)
    log.info { "Done executing commands for $audioFile" }
    // WARNING: we don't know how to check if the tag addition success
  }

  /**
   * Build the command line that will be executed to tag the audio file.
   *
   * The command line will be run under the current directory of this project (so that user can specify the relative path of the executable file).
   * @param audioFile Path the audio file to tag.
   * @param allInfo VSongTask all info of the song.
   * @return List<String> the command line to execute.
   */
  abstract fun buildCommand(audioFile: Path, allInfo: VSongTask): List<String>

  protected open fun executeCommand(commands: List<String>) {
    runCmd(commands).sync(processConfig.timeout, processConfig.unit, threadPool) {
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
  }
}

private val log = KInlineLogging.logger()
