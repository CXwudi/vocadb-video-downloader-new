package mikufan.cx.vvd.extractor.component

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.commonkt.naming.SongProperFileName
import mikufan.cx.vvd.extractor.config.IOConfig
import mikufan.cx.vvd.extractor.config.Preference
import mikufan.cx.vvd.extractor.model.VSongTask
import mikufan.cx.vvd.extractor.util.OrderConstants
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import kotlin.io.path.copyTo
import kotlin.io.path.div
import kotlin.io.path.extension
import kotlin.io.path.moveTo

/**
 * @date 2022-07-19
 * @author CX无敌
 */
@Component
@Order(OrderConstants.EXTRACTOR_RUNNER_ORDER)
class ExtractorRunner(
  ioConfig: IOConfig,
  preference: Preference,
) : RecordProcessor<VSongTask, VSongTask> {

  private val inputDirectory = ioConfig.inputDirectory
  private val outputDirectory = ioConfig.outputDirectory

  private val retryOnExtraction = preference.retryOnExtraction
  
  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    val parameters = record.payload.parameters
    val label = record.payload.label
    val baseFileName = parameters.songProperFileName
    val chosenAudioExtractorOpt = requireNotNull(parameters.chosenAudioExtractor) { "null optional audio extractor?" }
    log.info { "Start running chosen extractor or skip for $baseFileName" }
    // although audio extractor does give a proper file name to the extracted audio file, it is only for extractor purpose
    // we need a final audio file that is for the user to use, play and store
    val finalAudioFileBaseName = buildFinalAudioFileBaseName(baseFileName, label)
    if (chosenAudioExtractorOpt.isPresent) { // for vsong task that need extraction from PV, perform extraction and rename
      val chosenAudioExtractor = chosenAudioExtractorOpt.get()
      log.info { "${chosenAudioExtractor.name} is in charge of extraction of $baseFileName" }
      val failures = mutableListOf<Exception>()

      repeat(retryOnExtraction) { i -> 
        log.info { "start attempt ${i + 1}" }
        chosenAudioExtractor.extract(
          inputDirectory / label.pvFileName,
          record.payload,
          outputDirectory
        ).fold(
          onSuccess = { extractedAudioFile ->
            val finalAudioFile = outputDirectory / "$finalAudioFileBaseName.${extractedAudioFile.extension}"
            extractedAudioFile.moveTo(finalAudioFile, overwrite = true)
            parameters.finalAudioFile = finalAudioFile
            log.info { "Extracted audio file is $extractedAudioFile, moved to $finalAudioFile" }
            return record
          },
          onFailure =  {
            if (it !is Exception) { throw it }
            log.warn { "Failed to extract audio file for $baseFileName on attempt ${i + 1}" }
            failures.add(it)
          }
        )
      }

      log.error { "All extraction attempt on $baseFileName by ${chosenAudioExtractor.name} failed" }
      throw RuntimeVocaloidException(
          "All extraction attempt on $baseFileName by ${chosenAudioExtractor.name} failed, " +
          "exception list: ${failures.joinToString(prefix = "[", postfix = "]")}"
      )
    } else { // for vsong task that already has the audio file, we simply just copy over the audio file
      log.info { "No audio extraction need to performed for $baseFileName" }
      val audioFile = inputDirectory / label.audioFileName
      val finalAudioFile = outputDirectory / "$finalAudioFileBaseName.${audioFile.extension}"
      audioFile.copyTo(finalAudioFile, overwrite = true)
      parameters.finalAudioFile = finalAudioFile
      log.info { "Simply copy the existing audio file $audioFile to final audio file $finalAudioFile" }
    }
    return record
  }

  private fun buildFinalAudioFileBaseName(
    baseFileName: SongProperFileName,
    label: VSongLabel
  ): String = "$baseFileName [${label.pvService} ${label.pvId}]"
}

private val log = KInlineLogging.logger()
