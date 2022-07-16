package mikufan.cx.vvd.extractor.component.extractor.base

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.common.naming.FileNamePostFix
import mikufan.cx.vvd.commonkt.naming.SongProperFileName
import mikufan.cx.vvd.commonkt.naming.renameWithSameExtension
import mikufan.cx.vvd.extractor.model.VSongTask
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicInteger

/**
 * The base class for all audio extractors, that extract the audio track to a file from a video file.
 *
 * Implementation must be stateless to allow parallel execution of multiple files.
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
   * @return Result<Path> either a [Result.Failure] with exception or the path of the extracted audio file
   * @throws InterruptedException most likely if user presses ctrl+c
   */
  fun extract(pvFile: Path, allInfo: VSongTask, outputDirectory: Path): Result<Path> {
    val baseFileName = allInfo.parameters.songProperFileName
    return try {
      log.info { "Start extracting audio track from $pvFile to $outputDirectory with base file name $baseFileName" }
      val uniqueBaseFileName = "$baseFileName-i${idAccumulator.getAndIncrement()}"
      val extractedAudioFile = tryExtract(pvFile, uniqueBaseFileName, outputDirectory)
      val movedAudioFile = extractedAudioFile.renameWithSameExtension(baseFileName.toAudioFileName())
      log.info { "Extract success =￣ω￣= for $baseFileName, we got $movedAudioFile" }
      Result.success(movedAudioFile)
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
   * The method returns the path of the extracted audio file to indicate it succeeds.
   * Otherwise, throw exception to indicate it fails.
   *
   * This method can be ported to other projects if anyone wants to use it in their own project
   *
   * @param inputPvFile Path the PV file to be extracted
   * @param baseOutputFileName String the base file name of the output audio file, without extension.
   * such file name must be unique enough so that only related file contain this string.
   * this string is already normalized and safe to be a filename. no other normalization needed.
   * @param outputDirectory Path the directory to save the extracted audio track
   * @return Path the path of the extracted audio file
   * @throws InterruptedException most likely if user presses ctrl+c
   * @throws Exception if any other error occurs
   */
  internal abstract fun tryExtract(inputPvFile: Path, baseOutputFileName: String, outputDirectory: Path): Path
}

internal fun SongProperFileName.toAudioFileName(extensionWithDot: String = ""): String =
  this.toString() + FileNamePostFix.AUDIO + extensionWithDot

private val idAccumulator = AtomicInteger(0)

private val log = KInlineLogging.logger()
