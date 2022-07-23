package mikufan.cx.vvd.extractor.component.extractor.impl

import mikufan.cx.vvd.extractor.component.extractor.base.BaseCliAudioExtractor
import mikufan.cx.vvd.extractor.config.EnvironmentConfig
import mikufan.cx.vvd.extractor.config.ProcessConfig
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.nio.file.Path
import java.util.concurrent.ThreadPoolExecutor
import kotlin.io.path.exists

/**
 * The lossless audio extractor for any video with opus audio track (or any ogg/opus related audio codec).
 * Extracted audio will be in ogg format.
 * Although it is preferred to extracted as opus, but since NetEase Cloud Music does not support opus, it will be extracted as ogg.
 *
 * It will run this command:
 * ffmpeg -i input.mkv -vn -acodec copy output.ogg
 *
 * @date 2022-07-16
 * @author CX无敌
 */
@Component
class OpusToOggAudioExtractor(
  processConfig: ProcessConfig,
  environmentConfig: EnvironmentConfig,
  @Qualifier("extractorThreadPool") threadPool: ThreadPoolExecutor
) : BaseCliAudioExtractor(processConfig, threadPool) {

  private val ffmpegLaunchCmd = environmentConfig.ffmpegLaunchCmd

  /**
   * the name of the audio extractor
   */
  override val name: String = "Ogg Audio Extractor by FFmpeg"

  override fun buildCommand(inputPvFile: Path, baseOutputFileName: String, outputDirectory: Path): List<String> = buildList {
    addAll(ffmpegLaunchCmd)
    add("-i")
    add(inputPvFile.toString())
    add("-vn")
    add("-acodec")
    add("copy")
    add("-y")
    add(buildFinalAudioFile(outputDirectory, baseOutputFileName).toString())
  }

  override fun findExtractedAudioFile(outputDirectory: Path, baseOutputFileName: String): Path {
    val extractedAudioFile = buildFinalAudioFile(outputDirectory, baseOutputFileName)
    if (extractedAudioFile.exists()) {
      return extractedAudioFile
    } else {
      throw IllegalStateException("extracted audio file not found: $extractedAudioFile")
    }
  }

  private fun buildFinalAudioFile(
    outputDirectory: Path,
    baseOutputFileName: String
  ) = outputDirectory.resolve("$baseOutputFileName.ogg")
}
