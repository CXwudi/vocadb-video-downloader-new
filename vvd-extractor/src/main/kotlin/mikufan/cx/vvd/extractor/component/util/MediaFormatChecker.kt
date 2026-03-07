package mikufan.cx.vvd.extractor.component.util

import tools.jackson.databind.JsonNode
import tools.jackson.databind.ObjectMapper
import mikufan.cx.executil.runCmd
import mikufan.cx.executil.sync
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.extractor.config.EnvironmentConfig
import mikufan.cx.vvd.extractor.config.ProcessConfig
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
  @param:Qualifier("mediainfoThreadPool")
  private val threadPool: ThreadPoolExecutor,
) {

  private val mediainfoLaunchCmd = environmentConfig.mediainfoLaunchCmd

  /**
   * Check the audio format of the given file.
   *
   * List of common audio format from video format:
   *  - mp4/flv/ts -> aac
   *  - webm/mkv -> opus/aac/flac (opus from youtube-dl/yt-dlp with ffmpeg, without ffmpeg, it would be aac)
   * @param mediaFile Path the media file.
   * @return String the audio format, in smaller case letters.
   * @throws RuntimeVocaloidException if the file doesn't contain any audio track.
   */
  fun checkAudioFormat(mediaFile: Path): String {
    log.debug { "checking the format of the audio track through mediainfo for $mediaFile" }
    val cmd = buildList {
      addAll(mediainfoLaunchCmd)
      add("--output=JSON")
      add(mediaFile.toString())
    }
    val sb = StringBuilder()
    runCmd(cmd).sync(processConfig.timeout, processConfig.unit, threadPool) {
      onStdOut { sb.append(this.readText()) }
    }
    val mediainfoJson = objectMapper.readTree(sb.toString())
    val tracks = mediainfoJson["media"]["track"]
    if (tracks.size() <= 1) {
      throw RuntimeVocaloidException("mediainfo shows $mediaFile has no tracks: $mediainfoJson")
    }
    val audioTrack = tracks.firstOrNull { it["@type"].asString("").lowercase() == "audio" }
      ?: throw RuntimeVocaloidException("mediainfo shows $mediaFile has no audio track: $mediainfoJson")
    val audioFormat = audioTrack["Format"].asString("").lowercase()
    log.debug { "mediainfo shows $mediaFile has audio track with format $audioFormat" }
    return audioFormat
  }

  fun checkImageType(imageFile: Path): String {
    log.debug { "checking the mimetype of the thumbnail file through mediainfo for $imageFile" }
    val cmd = buildList {
      addAll(mediainfoLaunchCmd)
      add("--output=JSON")
      add(imageFile.toString())
    }
    val sb = StringBuilder()
    runCmd(cmd).sync(processConfig.timeout, processConfig.unit, threadPool) {
      onStdOut { sb.append(this.readText()) }
    }
    val mediainfoJson = objectMapper.readTree(sb.toString())
    val tracks = mediainfoJson["media"]["track"]
    if (tracks.size() <= 1) {
      throw RuntimeVocaloidException("mediainfo shows $imageFile has no tracks: $mediainfoJson")
    }
    val imageTrack = tracks.firstOrNull { it["@type"].asString("").lowercase() == "image" }
      ?: throw RuntimeVocaloidException("mediainfo shows $imageFile has no image: $mediainfoJson")
    val imageMimeType = normalizeImageType(imageTrack["Format"].asString(""))
    log.debug { "mediainfo shows $imageFile has image with mimetype $imageMimeType" }
    return imageMimeType
  }
}

/**
 * Normalize image type from codec name to container format.
 *
 * MediaInfo 25.04+ reports WebP files by their internal codec name (vp8, vp8l, vp8x)
 * instead of the container format (webp).
 */
private fun normalizeImageType(rawFormat: String): String {
  val lowerFormat = rawFormat.lowercase()
  return when (lowerFormat) {
    "vp8", "vp8l", "vp8x" -> "webp"  // WebP codecs
    "jpg", "jpe" -> "jpeg"           // JPEG variants
    else -> lowerFormat
  }
}

private val log = KInlineLogging.logger()
