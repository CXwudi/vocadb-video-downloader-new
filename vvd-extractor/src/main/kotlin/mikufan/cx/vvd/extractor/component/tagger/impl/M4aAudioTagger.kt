package mikufan.cx.vvd.extractor.component.tagger.impl

import mikufan.cx.vvd.extractor.component.tagger.base.BaseInternalPythonMutagenAudioTagger
import mikufan.cx.vvd.extractor.config.EnvironmentConfig
import mikufan.cx.vvd.extractor.config.IOConfig
import mikufan.cx.vvd.extractor.config.ProcessConfig
import mikufan.cx.vvd.extractor.model.VSongTask
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.nio.file.Path
import java.util.concurrent.ThreadPoolExecutor
import kotlin.io.path.div

/**
 * @date 2022-07-30
 * @author CX无敌
 */
@Component
class M4aAudioTagger(
  ioConfig: IOConfig,
  environmentConfig: EnvironmentConfig,
  processConfig: ProcessConfig,
  @Qualifier("taggerThreadPool") threadPool: ThreadPoolExecutor
) : BaseInternalPythonMutagenAudioTagger(environmentConfig, processConfig, threadPool) {

  private val inputDirectory = ioConfig.inputDirectory

  /**
   * Name of the audio tagger
   */
  override val name: String = "M4a Audio Tagger by Python Mutagen"

  /**
   * name of the script in resources/python directory.
   */
  override val pythonScriptFileName: String = "tag_m4a.py"

  /**
   * Build the command line arguments portion of the command line in [buildCommand].
   * @param audioFile Path the audio file to tag.
   * @param allInfo VSongTask all info of the song.
   * @return List<String> the command line arguments.
   */
  override fun buildArguments(audioFile: Path, allInfo: VSongTask): List<String> = buildList {
    val label = allInfo.label
    val infoFile = inputDirectory / label.infoFileName
    val labelFile = inputDirectory / label.labelFileName
    val thumbnailFile = inputDirectory / label.thumbnailFileName
    val audioExtractorName = requireNotNull(allInfo.parameters.chosenAudioExtractor) { "null audio extractor for $audioFile? " }
      .map { it.name } // get the name of the audio extractor
      .orElse("No Extractor") // if the optional is null, it means the audio itself is there, not from extraction

    add("-i")
    add(audioFile.toString())
    add("-l")
    add(labelFile.toString())
    add("-t")
    add(thumbnailFile.toString())
    add("-if")
    add(infoFile.toString())
    add("-aen")
    add(audioExtractorName)
    add("-atn")
    add(name)
  }
}
