package mikufan.cx.vvd.extractor.component.tagger.base

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.extractor.config.EnvironmentConfig
import mikufan.cx.vvd.extractor.config.ProcessConfig
import mikufan.cx.vvd.extractor.model.VSongTask
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.ThreadPoolExecutor
import javax.annotation.PreDestroy
import kotlin.io.path.bufferedWriter

/**
 * Base audio tagger powered by python mutagen library.
 *
 * It runs a python script in the resources directory to tag the audio file.
 * @date 2022-07-27
 * @author CX无敌
 */
abstract class BaseInternalPythonMutagenAudioTagger(
  environmentConfig: EnvironmentConfig,
  processConfig: ProcessConfig,
  threadPool: ThreadPoolExecutor,
) : BaseCliAudioTagger(processConfig, threadPool) {

  private val pythonLaunchCmd = environmentConfig.pythonLaunchCmd

  /**
   * name of the script in resources/python directory.
   */
  abstract val pythonScriptFileName: String

  private val loadedPythonScriptFile: Path by lazy {
    requireNotNull(this::class.java.classLoader.getResourceAsStream("python/$pythonScriptFileName")) {
      "$pythonScriptFileName not found in resources/python directory"
    }.bufferedReader().let { reader ->
      val tempPythonFile = Files.createTempFile("python", ".py")
      tempPythonFile.bufferedWriter().use { writer ->
        reader.forEachLine { // forEachLine already called use {}
          writer.write(it)
          writer.newLine()
        }
      }
      log.debug { "Loaded $pythonScriptFileName to temp file $tempPythonFile" }
      tempPythonFile
    }
  }

  override fun buildCommand(audioFile: Path, allInfo: VSongTask): List<String> = buildList {
    addAll(pythonLaunchCmd)
    add(loadedPythonScriptFile.toString())
    addAll(buildArguments(audioFile, allInfo))
  }

  /**
   * Build the command line arguments portion of the command line in [buildCommand].
   * @param audioFile Path the audio file to tag.
   * @param allInfo VSongTask all info of the song.
   * @return List<String> the command line arguments.
   */
  abstract fun buildArguments(audioFile: Path, allInfo: VSongTask): List<String>
  
  @PreDestroy
  fun deleteTempPythonFile() {
    log.debug { "Deleting temp python script file $loadedPythonScriptFile from $pythonScriptFileName" }
    Files.deleteIfExists(loadedPythonScriptFile)
  }
}

private val log = KInlineLogging.logger()
