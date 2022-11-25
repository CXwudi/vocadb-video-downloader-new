package mikufan.cx.vvd.extractor.component.extractor.impl

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.extractor.component.extractor.base.BaseCliAudioExtractor
import mikufan.cx.vvd.extractor.config.EnvironmentConfig
import mikufan.cx.vvd.extractor.config.ProcessConfig
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.nio.file.Path
import java.util.concurrent.ThreadPoolExecutor
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists

/**
 * The lossless audio extractor for any video with AAC LC audio track.
 * Extracted audio will be in m4a format.
 *
 * It execute two commands:
 * 1. ffmpeg -i input.mp4 -vn -acodec copy -y temp.aac
 * 2. ffmpeg -i temp.aac -vn --acodec copy -y -movflags +faststart output.m4a
 *
 * @date 2022-07-16
 * @author CX无敌
 */
@Component
class AacToM4aAudioExtractor(
  processConfig: ProcessConfig,
  environmentConfig: EnvironmentConfig,
  @Qualifier("extractorThreadPool") threadPool: ThreadPoolExecutor
) : BaseCliAudioExtractor(processConfig, threadPool) {

  private val ffmpegLaunchCmd = environmentConfig.ffmpegLaunchCmd

  /**
   * the name of the audio extractor
   */
  override val name: String = "M4a Audio Extractor by FFmpeg"

  /**
   * modified the super class method to execute two commands
   *
   * in windows, java can't execute two commands connected by &&, it will only run the one before &&
   *
   * so we simply just execute two commands in two separated processes
   */
  override fun tryExtract(inputPvFile: Path, baseOutputFileName: String, outputDirectory: Path): Path {
    val tempAacFile = buildTempAacFile(outputDirectory, baseOutputFileName)

    val aacCommand = buildAacCommand(inputPvFile, tempAacFile)
    log.info { "Executing first commands to get temp aac: ${aacCommand.joinToString(" ", "`", "`")}" }
    executeCommand(aacCommand)
    log.info { "Done executing temp acc command for $baseOutputFileName" }

    val m4aCommand = buildM4aCommand(tempAacFile, baseOutputFileName, outputDirectory)
    log.info { "Next, get final m4a file by executing: ${m4aCommand.joinToString(" ", "`", "`")}" }
    executeCommand(m4aCommand)
    log.info { "Done executing m4a commands for $baseOutputFileName" }

    tempAacFile.deleteIfExists()
    // find the extracted audio file
    return findExtractedAudioFile(outputDirectory, baseOutputFileName)
  }

  /**
   * not needed for this class as we use [buildAacCommand] and [buildM4aCommand]
   */
  override fun buildCommand(inputPvFile: Path, baseOutputFileName: String, outputDirectory: Path): List<String> {
    throw UnsupportedOperationException("Not needed")
  }

  private fun buildAacCommand(inputPvFile: Path, tempAacFile: Path): List<String> = buildList {
    addAll(ffmpegLaunchCmd)
    add("-i")
    add(inputPvFile.toString())
    add("-vn")
    add("-acodec")
    add("copy")
    add("-y")
    add(tempAacFile.toString())
  }

  private fun buildM4aCommand(tempAacFile: Path, baseOutputFileName: String, outputDirectory: Path): List<String> = buildList {
    addAll(ffmpegLaunchCmd)
    add("-i")
    add(tempAacFile.toString())
    add("-vn")
    add("-acodec")
    add("copy")
    add("-y")
    // simulate MP4Box way of extracting m4a
    add("-movflags")
    add("+faststart")
    add(buildFinalM4aFile(outputDirectory, baseOutputFileName).toString())
  }

  override fun findExtractedAudioFile(outputDirectory: Path, baseOutputFileName: String): Path {
    val extractedAudioFile = buildFinalM4aFile(outputDirectory, baseOutputFileName)
    if (extractedAudioFile.exists()) {
      return extractedAudioFile
    } else {
      throw IllegalStateException("Extracted audio file $extractedAudioFile not found")
    }
  }

  private fun buildTempAacFile(
    outputDirectory: Path,
    baseOutputFileName: String
  ): Path = outputDirectory.resolve("$baseOutputFileName-temp.aac")

  private fun buildFinalM4aFile(
    outputDirectory: Path,
    baseOutputFileName: String
  ): Path = outputDirectory.resolve("$baseOutputFileName.m4a")
}

private val log = KInlineLogging.logger()
