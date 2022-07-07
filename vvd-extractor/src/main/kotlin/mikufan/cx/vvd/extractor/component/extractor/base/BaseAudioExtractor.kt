package mikufan.cx.vvd.extractor.component.extractor.base

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.extractor.model.VSongTask
import java.nio.file.Path

/**
 * The base class for all audio extractors.
 *
 * If you want to create a new extractor, you should extend this class or any sub-baseclass of this class.
 *
 * If you want to use it in other project, simply remove the [extract] method to remove any dependency with VocaDB
 * and any other dependencies in this project.
 *
 * @date 2022-06-15
 * @author CX无敌
 */
abstract class BaseAudioExtractor {

  /**
   * the name of the audio extractor
   */
  abstract val name: String

  /**
   * Extract the audio track from the PV file of the song to the [outputDirectory].
   *
   * As always, even though this method sounds like can be moved to extractmanager but we
   * decide to put it here to allow the maximum flexibility of the future extractor implementation
   * @param pvFile Path the PV file to be extracted
   * @param allInfo VSongTask all information of the song
   * @param outputDirectory Path the directory to save the extracted audio track
   * @return Result<Path> the path of the extracted audio track
   */
  fun extract(pvFile: Path, allInfo: VSongTask, outputDirectory: Path): Result<Path> {
    val baseFileName = allInfo.parameters.songProperFileName
    return try {
      Result.success(tryExtract(pvFile, baseFileName.toString(), outputDirectory))
    } catch (e: InterruptedException) {
      Thread.currentThread().interrupt()
      log.error { "Extraction for $baseFileName gets interrupted" }
      throw e
    } catch (e: Exception) {
      log.error(e) { "Extraction for $baseFileName failed with error :(" }
      Result.failure(e)
    }
  }

  /**
   * Extract the audio track from the PV file of the song to the [outputDirectory] with a base file name [baseOutputFileName].
   *
   * This method can be ported to other projects if anyone wants to use it in their own project
   *
   * @param inputPvFile Path the PV file to be extracted
   * @param baseOutputFileName String the base file name of the output file, without extension
   * @param outputDirectory Path the directory to save the extracted audio track
   * @return Path the path of the extracted audio track
   */
  internal abstract fun tryExtract(inputPvFile: Path, baseOutputFileName: String, outputDirectory: Path): Path
}

private val log = KInlineLogging.logger()