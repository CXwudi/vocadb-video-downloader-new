package mikufan.cx.vvd.extractor.component

import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.extractor.component.extractor.base.BaseAudioExtractor
import mikufan.cx.vvd.extractor.component.extractor.impl.AacToM4aAudioExtractor
import mikufan.cx.vvd.extractor.component.extractor.impl.OpusToOggAudioExtractor
import mikufan.cx.vvd.extractor.component.tagger.base.BaseAudioTagger
import mikufan.cx.vvd.extractor.component.tagger.impl.M4aAudioTagger
import mikufan.cx.vvd.extractor.component.tagger.impl.Mp3AudioTagger
import mikufan.cx.vvd.extractor.component.tagger.impl.OggOpusAudioTagger
import mikufan.cx.vvd.extractor.model.VSongTask
import mikufan.cx.vvd.extractor.util.AudioMediaFormat
import mikufan.cx.vvd.extractor.util.OrderConstants
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.nio.file.Path
import java.util.*

/**
 * @date 2022-07-26
 * @author CX无敌
 */
@Component
@Order(OrderConstants.TAGGER_DECIDER_ORDER)
class TaggerDecider(
  private val taggerDeciderCore: TaggerDeciderCore,
) : RecordProcessor<VSongTask, VSongTask> {

  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    val parameters = record.payload.parameters
    val decidedExtractorOpt = requireNotNull(parameters.chosenAudioExtractor) { "null chosenAudioExtractor?" }
    parameters.chosenAudioTagger = taggerDeciderCore.decideTagger(decidedExtractorOpt, parameters.processedAudioFile)
    return record
  }
}

@Component
class TaggerDeciderCore(
  private val audioMediaFormatChecker: MediaFormatChecker,
  private val beanFactory: ApplicationContext,
) {

  fun decideTagger(
    decidedExtractorOpt:  Optional<BaseAudioExtractor>,
    processedAudioFile: Path?,
  ): BaseAudioTagger = if (decidedExtractorOpt.isPresent) {
    // the format of the audio file produced by audio extractor is pretty much fixed, so we can simply map it to correct tagger
    when (val decidedExtractor = decidedExtractorOpt.get()) {
      is AacToM4aAudioExtractor -> beanFactory.getBean<M4aAudioTagger>()
      is OpusToOggAudioExtractor -> beanFactory.getBean<OggOpusAudioTagger>()
      else -> error("This should not happened, unknown audio extractor: $decidedExtractor")
    }
  } else {
    // no audio extractor = is using audio file copied from label to outputDirectory
    val audioFile = requireNotNull(processedAudioFile) { "null processedAudioFile?" }
    when (val audioFormat = audioMediaFormatChecker.checkAudioFormat(audioFile)) {
      AudioMediaFormat.AAC -> beanFactory.getBean<M4aAudioTagger>()
      AudioMediaFormat.OPUS -> beanFactory.getBean<OggOpusAudioTagger>()
      AudioMediaFormat.MPEG_AUDIO -> beanFactory.getBean<Mp3AudioTagger>()
      else -> throw RuntimeVocaloidException("Audio format $audioFormat is not supported from $audioFile")
    }
  }
}
