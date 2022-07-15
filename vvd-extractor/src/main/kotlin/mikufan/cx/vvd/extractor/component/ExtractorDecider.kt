package mikufan.cx.vvd.extractor.component

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import mikufan.cx.executil.runCmd
import mikufan.cx.executil.sync
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.extractor.config.EnvironmentConfig
import mikufan.cx.vvd.extractor.config.IOConfig
import mikufan.cx.vvd.extractor.model.VSongTask
import mikufan.cx.vvd.extractor.util.OrderConstants
import org.apache.commons.lang3.mutable.MutableObject
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.nio.file.Path
import java.util.*
import kotlin.io.path.absolute
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.notExists

/**
 * @date 2022-07-01
 * @author CX无敌
 */
@Component
@Order(OrderConstants.EXTRACTOR_DECIDER_ORDER)
class ExtractorDecider(
  ioConfig: IOConfig,
  environmentConfig: EnvironmentConfig,
  private val objectMapper: ObjectMapper,
  private val ctx: ApplicationContext,
) : RecordProcessor<VSongTask, VSongTask> {

  private val inputDirectory = ioConfig.inputDirectory
  private val mediainfoLaunchCmd = environmentConfig.mediainfoLaunchCmd

  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    val baseFileName = record.payload.parameters.songProperFileName
    log.info { "Start deciding the best audio extractor for $baseFileName" }
    // if the label contains a valid audio file, then skip extraction
    val audioFile = inputDirectory / record.payload.label.audioFileName
    if (audioFile.exists()) {
      log.info { "Skip choosing audio extractor for $baseFileName as it contains an audio file $audioFile" }
      return record.apply {
        payload.parameters.chosenAudioExtractor = Optional.empty()
      }
    }
    val pvFile = inputDirectory / record.payload.label.pvFileName
    if (pvFile.notExists()) {
      throw RuntimeVocaloidException(
        "pv file not found: ${pvFile.absolute()} for song $baseFileName. " +
            "Nor does it has a valid audio file."
      )
    }
    val audioFormat = checkAudioFormat(pvFile)
    /*
    list of common audio format from video format
    mp4/flv -> aac
    webm,mkv -> opus/aac/flac (opus from youtube-dl/yt-dlp with ffmpeg, without ffmpeg, it would be aac)
     */
    TODO("continue back here when finished all audio extractors")
    return record
  }

  /**
   * Run the MediaInfo to check the format of the audio file
   * @param pvFile Path the pv file to check the format of
   * @return String the format of the audio file
   */
  internal fun checkAudioFormat(pvFile: Path): String {
    log.debug { "checking the format of the audio track through mediainfo for $pvFile" }
    val cmd = buildList {
      addAll(mediainfoLaunchCmd)
      add("--output=JSON")
      add(pvFile.toString())
    }.toTypedArray()
    val jsonHolder = MutableObject<JsonNode>()
    runCmd(*cmd).sync {
      onStdOut { jsonHolder.value = objectMapper.readTree(this) }
    }
    val mediainfoJson = jsonHolder.value
    val tracks = mediainfoJson["media"]["track"]
    if (tracks.size() <= 1) {
      throw RuntimeVocaloidException("mediainfo shows $pvFile has no tracks: $mediainfoJson")
    }
    val audioTrack = tracks.firstOrNull { it["@type"].asText().lowercase() == "audio" }
      ?: throw RuntimeVocaloidException("mediainfo shows $pvFile has no audio track: $mediainfoJson")
    val audioFormat = audioTrack["Format"].asText().lowercase()
    log.debug { "mediainfo shows $pvFile has audio track with format $audioFormat" }
    return audioFormat
  }
}

private val log = KInlineLogging.logger()
