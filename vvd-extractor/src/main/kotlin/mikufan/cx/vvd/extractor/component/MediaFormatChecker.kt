package mikufan.cx.vvd.extractor.component

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import mikufan.cx.executil.runCmd
import mikufan.cx.executil.sync
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.extractor.config.EnvironmentConfig
import mikufan.cx.vvd.extractor.config.ProcessConfig
import org.apache.commons.lang3.mutable.MutableObject
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.nio.file.Path
import java.util.concurrent.ThreadPoolExecutor

/**
 * A wrapper of mediaInfo, shared by multiple components.
 *
 * @date 2022-07-23
 * @author CX无敌
 */
@Component
class MediaFormatChecker(
  environmentConfig: EnvironmentConfig,
  private val objectMapper: ObjectMapper,
  private val processConfig: ProcessConfig,
  @Qualifier("mediainfoThreadPool")
  private val threadPool: ThreadPoolExecutor,
) {

  private val mediainfoLaunchCmd = environmentConfig.mediainfoLaunchCmd

  fun checkAudioFormat(pvFile: Path): String {
    log.debug { "checking the format of the audio track through mediainfo for $pvFile" }
    val cmd = buildList {
      addAll(mediainfoLaunchCmd)
      add("--output=JSON")
      add(pvFile.toString())
    }
    val jsonHolder = MutableObject<JsonNode>()
    runCmd(cmd).sync(processConfig.timeout, processConfig.unit, threadPool) {
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
