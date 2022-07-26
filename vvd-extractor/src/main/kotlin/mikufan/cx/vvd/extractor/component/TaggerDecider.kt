package mikufan.cx.vvd.extractor.component

import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.extractor.component.extractor.impl.AacToM4aAudioExtractor
import mikufan.cx.vvd.extractor.component.extractor.impl.OpusToOggAudioExtractor
import mikufan.cx.vvd.extractor.model.VSongTask
import mikufan.cx.vvd.extractor.util.OrderConstants
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

/**
 * @date 2022-07-26
 * @author CX无敌
 */
@Component
@Order(OrderConstants.TAGGER_DECIDER_ORDER)
class TaggerDecider(
  private val audioMediaFormatChecker: MediaFormatChecker,
  private val ctx: ApplicationContext,
) : RecordProcessor<VSongTask, VSongTask> {

  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    val parameters = record.payload.parameters
    val decidedExtractorOpt = requireNotNull(parameters.chosenAudioExtractor) { "null chosenAudioExtractor?" }
    if (decidedExtractorOpt.isPresent) {
      // the format of the audio file produced by audio extractor is pretty much fixed, so we can simply map it to correct tagger
      when (val decidedExtractor = decidedExtractorOpt.get()) {
        is AacToM4aAudioExtractor -> TODO()
        is OpusToOggAudioExtractor -> TODO()
        else -> throw IllegalStateException("This should not happened, unknown audio extractor: $decidedExtractor")
      }
    } else {
      // no audio extractor = using audio file copied from label to outputDirectory
      val audioFile = requireNotNull(parameters.processedAudioFile) { "null processedAudioFile?" }
      when (val audioFormat = audioMediaFormatChecker.checkAudioFormat(audioFile)) {
        "aac" -> TODO()
        "opus" -> TODO()
        else -> throw RuntimeVocaloidException("Audio format: $audioFormat is not supported")
      }
    }
    return record
  }
}
