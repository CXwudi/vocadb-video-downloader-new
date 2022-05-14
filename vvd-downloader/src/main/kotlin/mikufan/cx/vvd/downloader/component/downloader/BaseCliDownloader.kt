package mikufan.cx.vvd.downloader.component.downloader

import com.fasterxml.jackson.databind.ObjectMapper
import mikufan.cx.executil.runCmd
import mikufan.cx.executil.sync
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.downloader.config.DownloadConfig
import mikufan.cx.vvd.downloader.config.EnvironmentConfig
import org.apache.tika.Tika
import java.nio.file.Path
import java.util.*
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.readText

/**
 * A common base class for downloader that execute external command lines tools
 *
 * Subclasses can simply override [buildCommands] to complete the download implementation.
 * Otherwise, considering extending [BaseDownloader] class instead
 *
 * @date 2022-05-14
 * @author CX无敌
 */
abstract class BaseCliDownloader(
  protected val downloadConfig: DownloadConfig,
  protected val tika: Tika,
  environmentConfig: EnvironmentConfig,
  protected val objectMapper: ObjectMapper,
) : BaseDownloader() {

  private val mediainfoLaunchCmd = environmentConfig.mediainfoLaunchCmd

  /**
   * download using command line, then use apache tika and mediainfo to identify the file type
   */
  override fun tryDownload(url: String, baseFileName: String, outputDirectory: Path): DownloadFiles {

    // build commands
    val commands = buildCommands(url, baseFileName, outputDirectory)

    // execute commands
    log.info { "Executing command: ${commands.joinToString(" ", "'", "'")}" }
    runCmd(*commands.toTypedArray()).sync(downloadConfig.timeout, downloadConfig.unit, threadPool) {
      onStdOutEachLine {
        if (it.isNotBlank()) {
          log.info { it }
        }
      }
      onStdErrEachLine {
        if (it.isNotBlank()) {
          log.debug { it }
        }
      }
    }

    // finds the downloaded files
    log.info { "Done command execution, detecting types for downloaded files" }
    val downloadedFiles: List<Path> = outputDirectory.listDirectoryEntries("*$baseFileName*")
    // identify which is PV, audio, thumbnails using apache tika and mediainfo
    val typePathMap = downloadedFiles.associateBy { detectType(it) }
    val pvFile = typePathMap[DownloadFileType.PV]
    val audioFile = typePathMap[DownloadFileType.AUDIO]
    val thumbnailFile = typePathMap[DownloadFileType.THUMBNAIL]
    if (typePathMap.contains(DownloadFileType.OTHERS)) {
      log.warn { "Found other files: ${typePathMap[DownloadFileType.OTHERS]}" }
    }
    if (pvFile == null && audioFile == null) {
      throw IllegalStateException("None of PV file and audio file is found")
    }
    if (thumbnailFile == null) {
      throw IllegalStateException("Thumbnail file is not found")
    }
    val downloadFiles = DownloadFiles(
      pvFile = pvFile,
      audioFile = audioFile,
      thumbnailFile = thumbnailFile,
    )
    log.info { "Done downloading $downloadFiles" }
    return downloadFiles
  }

  private fun detectType(it: Path): DownloadFileType {
    val type = tika.detect(it)
    return when {
      type.contains("video") || type.contains("matroska") -> DownloadFileType.PV // mkv and webp usually get detected as matroska by tika
      type.contains("audio") -> DownloadFileType.AUDIO
      type.contains("image") -> DownloadFileType.THUMBNAIL
      else -> { // perform mediainfo check using command line to identify the file type
        log.debug { "failed to detect using tika on ${it.fileName}" }
        val cmd = buildList {
          addAll(mediainfoLaunchCmd)
          add("--output=JSON")
          add(it.toString())
        }.toTypedArray()
        val sb = StringBuilder()

        log.debug { "running ${cmd.joinToString(" ", "'", "'")} to detect type" }
        runCmd(*cmd).sync {
          onStdOut { sb.append(it.readText()) }
        }
        val mediaInfoJson = objectMapper.readTree(sb.toString())
        // no need to check if the file exists, since we got the file from `outputDirectory.listDirectoryEntries()`
        val tracks = mediaInfoJson["media"]["track"]
        if (tracks.size() <= 1) { // confirmed that for image, video and audio files, the first track is always at index 1
          log.warn { "Find out the file ${it.fileName} is not a video or audio or image type" }
          return DownloadFileType.OTHERS
        } else {
          return when (val typeName = tracks[1]["@type"].asText().lowercase()) {
            "video" -> DownloadFileType.PV
            "audio" -> DownloadFileType.AUDIO
            "image" -> DownloadFileType.THUMBNAIL
            else -> {
              log.warn { "Find out the file ${it.fileName} is a wired type of $typeName" }
              DownloadFileType.OTHERS
            }
          }
        }
      }
    }
  }

  /**
   * Build the command line used to download the needed resources (pv/audio + thumbnail).
   *
   * The command line will be run under the current directory of this project (so that user can specify the relative path of the executable file).
   * Make sure specify the correct output path of downloaded files using [outputDirectory]
   *
   * The downloaded files must contain the string [baseFileName] in their file names (so that we can find these downloaded files).
   */
  abstract fun buildCommands(url: String, baseFileName: String, outputDirectory: Path): List<String>

  private val threadPool = ThreadPoolExecutor(
    3,
    3,
    downloadConfig.timeout,
    downloadConfig.unit,
    LinkedBlockingDeque(),
    ExternalProcessThreadFactory("$targetPvService-$downloaderName")
  )
}

private class ExternalProcessThreadFactory(baseName: String) : ThreadFactory {

  private val names = listOf("$baseName-stdout", "$baseName-stderr", "$baseName-stdin").toCollection(LinkedList())

  override fun newThread(r: Runnable): Thread {
    return Thread(r, names.pop())
  }
}

private val log = KInlineLogging.logger()
