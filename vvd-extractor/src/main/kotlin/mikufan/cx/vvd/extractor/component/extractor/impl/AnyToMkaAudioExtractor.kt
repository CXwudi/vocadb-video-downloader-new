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
 * The lossless audio extractor that simply package the audio track into a mka container. (Matroska Audio)
 * This container supports almost all audio codecs, including the super rare ones like EAC-3.
 *
 * It will run this command:
 * `ffmpeg -i input.<any format> -vn -acodec copy output.mka`
 *
 *
 * @author CXwudi with love to Miku
 * 2024-12-14
 */
@Component
class AnyToMkaAudioExtractor(
  processConfig: ProcessConfig,
  environmentConfig: EnvironmentConfig,
  @Qualifier("extractorThreadPool") threadPool: ThreadPoolExecutor
) : BaseCliAudioExtractor(processConfig, threadPool) {

  private val ffmpegLaunchCmd = environmentConfig.ffmpegLaunchCmd

  /**
   * the name of the audio extractor
   */
  override val name: String = "Mka Audio Extractor by FFmpeg"

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

  fun buildFinalAudioFile(
    outputDirectory: Path,
    baseOutputFileName: String
  ): Path = outputDirectory.resolve("$baseOutputFileName.mka")

  override fun findExtractedAudioFile(outputDirectory: Path, baseOutputFileName: String): Path {
    val extractedAudioFile = buildFinalAudioFile(outputDirectory, baseOutputFileName)
    if (extractedAudioFile.exists()) {
      return extractedAudioFile
    } else {
      throw IllegalStateException("extracted audio file not found: $extractedAudioFile")
    }
  }
}