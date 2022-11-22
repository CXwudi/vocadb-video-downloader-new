package mikufan.cx.vvd.extractor.component

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.extractor.config.IOConfig
import mikufan.cx.vvd.extractor.config.RetryPreference
import mikufan.cx.vvd.extractor.model.VSongTask
import mikufan.cx.vvd.extractor.util.OrderConstants
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import kotlin.io.path.copyTo
import kotlin.io.path.div

/**
 * @date 2022-07-19
 * @author CX无敌
 */
@Component
@Order(OrderConstants.EXTRACTOR_RUNNER_ORDER)
class ExtractorRunner(
  ioConfig: IOConfig,
  retryPreference: RetryPreference,
) : RecordProcessor<VSongTask, VSongTask> {

  private val inputDirectory = ioConfig.inputDirectory
  private val outputDirectory = ioConfig.outputDirectory

  private val retryOnExtraction = retryPreference.retryOnExtraction

  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    val parameters = record.payload.parameters
    val label = record.payload.label
    val baseFileName = parameters.songProperFileName
//    val songInfo = requireNotNull(parameters.songForApiContract) { "songForApiContract must not be null" }
    val chosenAudioExtractorOpt = requireNotNull(parameters.chosenAudioExtractor) { "null optional audio extractor?" }
    log.info { "Start running chosen extractor or skip for $baseFileName" }

    // val finalAudioFileBaseName = buildFinalAudioFileBaseName(songInfo, label)
    if (chosenAudioExtractorOpt.isPresent) { // for vsong task that need extraction from PV, perform extraction and rename
      val chosenAudioExtractor = chosenAudioExtractorOpt.get()
      log.info { "${chosenAudioExtractor.name} is in charge of extraction of $baseFileName" }
      val failures = mutableListOf<Exception>()

      // retry count is only counted for failed extraction, so +1 to always run extraction once
      repeat(retryOnExtraction + 1) { i ->
        log.debug { "  start attempt ${i + 1}" }
        chosenAudioExtractor.extract(
          inputDirectory / label.pvFileName,
          record.payload,
          outputDirectory
        ).fold(
          onSuccess = { extractedAudioFile ->
            parameters.processedAudioFile = extractedAudioFile
            log.info { "Extracted audio file is $extractedAudioFile" }
            return record
          },
          onFailure = {
            if (it !is Exception) {
              throw it
            }
            log.warn { "  Failed to extract audio file for $baseFileName on attempt ${i + 1}" }
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
      val finalAudioFile = outputDirectory / label.audioFileName
      audioFile.copyTo(finalAudioFile, overwrite = true)
      parameters.processedAudioFile = finalAudioFile
      log.info { "Simply copy the existing audio file $audioFile to final audio file $finalAudioFile" }
    }
    return record
  }

  /**
   * TODO: move the final renaming to a specific component, so that in future we can allow custom final naming
   */
//  private fun buildFinalAudioFileBaseName(
//    songInfo: SongForApiContract,
//    label: VSongLabel
//  ): String {
//    val finalFileNameBase = songInfo.let {
//      // this code is exactly same in [mikufan.cx.vvd.commonkt.naming.FileNameUtilKt.toProperFileName], but without the VocaDB ID
//      val artists: List<String> = requireNotNull(it.artistString) { "artist string is null" }.split("feat.")
//      val vocals = artists[1].trim()
//      val producers = artists[0].trim()
//      val songName: String = requireNotNull(it.defaultName) { "song name is null" }
//      removeIllegalChars(String.format("【%s】%s【%s】", vocals, songName, producers))
//    }
//    return "$finalFileNameBase - ${label.pvService} [${label.pvId}]"
//  }
}

private val log = KInlineLogging.logger()
