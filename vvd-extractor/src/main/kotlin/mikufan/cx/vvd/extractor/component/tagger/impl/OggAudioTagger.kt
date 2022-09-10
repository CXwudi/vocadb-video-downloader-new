package mikufan.cx.vvd.extractor.component.tagger.impl

import mikufan.cx.executil.redirectErrorStream
import mikufan.cx.executil.runCmd
import mikufan.cx.executil.sync
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.extractor.component.tagger.base.BaseInternalPythonMutagenAudioTagger
import mikufan.cx.vvd.extractor.config.EnvironmentConfig
import mikufan.cx.vvd.extractor.config.IOConfig
import mikufan.cx.vvd.extractor.config.ProcessConfig
import mikufan.cx.vvd.extractor.model.VSongTask
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.nio.file.Path
import java.util.concurrent.ThreadPoolExecutor
import kotlin.io.path.deleteIfExists
import kotlin.io.path.div
import kotlin.io.path.extension

/**
 * @date 2022-07-30
 * @author CX无敌
 */
@Component
class OggAudioTagger(
  ioConfig: IOConfig,
  environmentConfig: EnvironmentConfig,
  processConfig: ProcessConfig,
  @Qualifier("taggerThreadPool") threadPool: ThreadPoolExecutor,
) : BaseInternalPythonMutagenAudioTagger(environmentConfig, processConfig, threadPool) {

  private val inputDirectory = ioConfig.inputDirectory
  private val ffmpegLaunchCmd = environmentConfig.ffmpegLaunchCmd

  /**
   * Name of the audio tagger
   */
  override val name: String = "Ogg(Opus) Audio Tagger by Python Mutagen"

  /**
   * name of the script in resources/python directory.
   */
  override val pythonScriptFileName: String = "tag_oggopus.py"

//  /**
//   * override to support converting the image format if not supported
//   */
//  override fun tryTag(audioFile: Path, allInfo: VSongTask) {
//    withCorrectThumbnailImageFormat(allInfo) { imageCorrectedAllInfo ->
//      super.tryTag(audioFile, imageCorrectedAllInfo)
//    }
//  }

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
    val audioExtractorName =
      requireNotNull(allInfo.parameters.chosenAudioExtractor) { "null audio extractor for $audioFile? " }
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

  /**
   * use this to convert the thumbnail to a more common format if we needed
   *
   * if we need to, better add a new component just for converting the thumbnail.
   * that would require adding a new field in [VSongTask] to store the converted thumbnail
   */
  @Deprecated("not needed anymore")
  private inline fun withCorrectThumbnailImageFormat(allInfo: VSongTask, tryTagBlock: (allInfo: VSongTask) -> Unit) {
    val thumbnailFile = inputDirectory / allInfo.label.thumbnailFileName
    val thumbnailFormat = thumbnailFile.extension.lowercase()
    if (thumbnailFormat in listOf("jpg", "jpeg", "png")) {
      tryTagBlock(allInfo)
    } else {
      val pngThumbnailFile = thumbnailFile.resolveSibling(thumbnailFile.fileName.toString().split('.')[0] + ".png")
      log.info { "Preprocessing the thumbnail image to have a correct format, start converting from $thumbnailFile to $pngThumbnailFile" }
      val convertingCommands = buildList {
        addAll(ffmpegLaunchCmd)
        add("-i")
        add(thumbnailFile.toString())
        add(pngThumbnailFile.toString())
        add("-y")
      }
      runCmd(convertingCommands) {
        this.redirectErrorStream = true
      }.sync(processConfig.timeout, processConfig.unit, threadPool) {
        onStdOutEachLine {
          if (it.isNotBlank()) {
            log.debug { it }
          }
        }
      }
      tryTagBlock(
        allInfo.copy(
          // here we are building a new VSongTask with the new thumbnail file and all needed info.
          // fortunately, this works because audio tagger doesn't have returns
          label = VSongLabel.builder()
            .infoFileName(allInfo.label.infoFileName)
            .labelFileName(allInfo.label.labelFileName) // this would point to the original label json file
//            .thumbnailFileName(pngThumbnailFile.fileName.toString())
            .thumbnailFileName(thumbnailFile.fileName.toString())
            .build()
        )
      )
      pngThumbnailFile.deleteIfExists()
    }
  }
}

private val log = KInlineLogging.logger()
