package mikufan.cx.vvd.extractor.component

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.extractor.component.extractor.base.BaseAudioExtractor
import mikufan.cx.vvd.extractor.component.extractor.impl.AacToM4aAudioExtractor
import mikufan.cx.vvd.extractor.component.extractor.impl.AnyToMkaAudioExtractor
import mikufan.cx.vvd.extractor.component.extractor.impl.OpusToOggAudioExtractor
import mikufan.cx.vvd.extractor.config.IOConfig
import mikufan.cx.vvd.extractor.model.VSongTask
import mikufan.cx.vvd.extractor.util.OrderConstants
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*
import kotlin.io.path.absolute
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.notExists

@Component
@Order(OrderConstants.EXTRACTOR_DECIDER_ORDER)
class ExtractorDecider(
  private val extractorDeciderCore: ExtractorDeciderCore
) : RecordProcessor<VSongTask, VSongTask> {

  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    val task = record.payload
    val extractor = extractorDeciderCore.decideExtractor(
      audioFileName = task.label.audioFileName,
      videoFileName = task.label.pvFileName,
      baseFileName = task.parameters.songProperFileName.toString()
    )
    task.parameters.chosenAudioExtractor = Optional.ofNullable(extractor)
    return record
  }
}


/**
 * @date 2022-07-01
 * @author CX无敌
 */
@Component
class ExtractorDeciderCore(
  ioConfig: IOConfig,
  private val audioMediaFormatChecker: MediaFormatChecker,
  private val ctx: ApplicationContext,
) {

  private val inputDirectory = ioConfig.inputDirectory

  fun decideExtractor(audioFileName: String? = null, videoFileName: String, baseFileName: String): BaseAudioExtractor? {
    log.info { "Start deciding the best audio extractor for $baseFileName" }

    if (!audioFileName.isNullOrBlank()) {
      val audioFile = inputDirectory / audioFileName
      if (audioFile.exists()) {
        log.info { "Skip choosing audio extractor for $baseFileName as it contains an audio file $audioFile" }
        return null
      } else {
        log.warn { "Audio file $audioFileName is declared but doesn't exist in input directory, treating as no audio file" }
      }
    }

    val pvFile = inputDirectory / videoFileName
    if (pvFile.notExists()) {
      throw RuntimeVocaloidException(
        "pv file not found: ${pvFile.absolute()} for song $baseFileName. " +
            "Nor does it has a valid audio file."
      )
    }

    return when (val audioFormat = audioMediaFormatChecker.checkAudioFormat(pvFile)) {
      "aac" -> ctx.getBean<AacToM4aAudioExtractor>()
      "opus" -> ctx.getBean<OpusToOggAudioExtractor>()
      else -> {
        log.warn { "Unsupported audio format $audioFormat for $baseFileName, fallback to use mka extractor" }
        ctx.getBean<AnyToMkaAudioExtractor>()
      }
    }.also {
      log.info { "Decided to use ${it.name} to extract audio from $baseFileName" }
    }
  }
}


private val log = KInlineLogging.logger()
