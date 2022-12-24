package mikufan.cx.vvd.extractor.component

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.commonkt.naming.SongProperFileName
import mikufan.cx.vvd.extractor.component.tagger.base.BaseAudioTagger
import mikufan.cx.vvd.extractor.config.RetryPreference
import mikufan.cx.vvd.extractor.model.VSongTask
import mikufan.cx.vvd.extractor.util.OrderConstants
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.nio.file.Path

/**
 * @author CX无敌
 * 2022-11-08
 */
@Component
@Order(OrderConstants.TAGGER_RUNNER_ORDER)
class TagRunner(
  retryPreference: RetryPreference,
) : RecordProcessor<VSongTask, VSongTask> {

  private val tagRunnerCore = TagRunnerCore()
  private val retryOnTagging = retryPreference.retryOnTagging

  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    val parameters = record.payload.parameters
    val songBaseName = parameters.songProperFileName
    val audioTagger = requireNotNull(parameters.chosenAudioTagger) { "null optional audio tagger?" }
    val processedAudioFile = requireNotNull(parameters.processedAudioFile) { "null optional processed audio file?" }
    tagRunnerCore.doTagging(audioTagger, processedAudioFile, record.payload, retryOnTagging, songBaseName)
    return record
  }
}

class TagRunnerCore {

  /**
   * Run the [BaseAudioTagger] with given retry count, fail and throw exception if all retry failed
   *
   * @param audioTagger BaseAudioTagger to tag the audio file
   * @param targetFile Path of the audio file to be tagged
   * @param allInfo all info of the song that is represented by the audio file
   * @param retryCount Int number of retry if tagging failed
   * @param songBaseName String base name of the song, used for logging
   */
  fun doTagging(audioTagger: BaseAudioTagger, targetFile: Path, allInfo: VSongTask, retryCount: Int, songBaseName: SongProperFileName) {
    log.info { "Start running chosen audio tagger ${audioTagger.name} on $songBaseName" }
    val failures = mutableListOf<Exception>()

    // retry count is only counted for failed tagging, so +1 to always run tagging once
    repeat(retryCount + 1) { i ->
      log.debug { "  start attempt ${i + 1}" }
      audioTagger.tag(targetFile, allInfo).fold(
        onSuccess = {
          log.info { "Tagging of $songBaseName is successful" }
          return
        },
        onFailure = {
          if (it !is Exception) {
            throw it
          }
          log.warn { "  Failed to add tags for $songBaseName on attempt ${i + 1}" }
          failures.add(it)
        }
      )
    }
    log.error { "All extraction attempt on $songBaseName by ${audioTagger.name} failed" }
    throw RuntimeVocaloidException(
      "All extraction attempt on $songBaseName by ${audioTagger.name} failed, " +
          "exception list: ${failures.joinToString(prefix = "[", postfix = "]")}"
    )
  }
}

private val log = KInlineLogging.logger()
