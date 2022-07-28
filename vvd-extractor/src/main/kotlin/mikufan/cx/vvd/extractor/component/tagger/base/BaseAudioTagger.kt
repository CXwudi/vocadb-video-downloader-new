package mikufan.cx.vvd.extractor.component.tagger.base

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.extractor.model.VSongTask
import java.nio.file.Path

/**
 * Base class of all audio tagger
 *
 * Implementation must be stateless to support running multiple AudioTagger in parallel
 *
 * Unfortunately, you can't easily port the tagging code to other project,
 * but since most tagger implementation is done by python mutagen script, it is best to
 * just copy the python script stored in the resource directory in this module
 *
 * @date 2022-07-26
 * @author CX无敌
 */
abstract class BaseAudioTagger {

  /**
   * Name of the audio tagger
   */
  abstract val name: String

  /**
   * Add tags to the audio file using information from [allInfo]
   * @param audioFile Path the audio file to be tagged
   * @param allInfo VSongTask all information of the song
   * @return `Result<Unit>` no turn if success, otherwise return a failed result with exception
   * @throws InterruptedException most likely if user presses ctrl+c
   */
  fun tag(audioFile: Path, allInfo: VSongTask): Result<Unit> {
    return try {
      log.info { "Start adding tags to audio file $audioFile" }
      tryTag(audioFile, allInfo)
      log.info { "Tag added success =￣ω￣= for $audioFile" }
      return Result.success(Unit)
    } catch (e: InterruptedException) {
      Thread.currentThread().interrupt()
      log.error { "Tagging for $audioFile gets interrupted" }
      throw e
    } catch (e: Exception) {
      log.error(e) { "Tagging for $audioFile failed with error :(" }
      Result.failure(e)
    }
  }

  /**
   * Pretty much the actual implementation of [tag]
   *
   * So this method is not portable
   *
   * @throws InterruptedException most likely if user presses ctrl+c
   * @throws Exception if any other error occurs
   */
  internal abstract fun tryTag(audioFile: Path, allInfo: VSongTask)
}

private val log = KInlineLogging.logger()
