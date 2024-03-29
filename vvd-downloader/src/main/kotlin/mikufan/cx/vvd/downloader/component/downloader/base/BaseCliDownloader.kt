package mikufan.cx.vvd.downloader.component.downloader.base

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PreDestroy
import mikufan.cx.executil.runCmd
import mikufan.cx.executil.sync
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.commonkt.threading.ExternalProcessThreadFactory
import mikufan.cx.vvd.downloader.config.DownloadConfig
import mikufan.cx.vvd.downloader.config.EnvironmentConfig
import org.apache.tika.Tika
import java.nio.file.Path
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import kotlin.io.path.listDirectoryEntries

/**
 * A common base class for downloader that execute external command lines tools
 *
 * Subclasses can simply override [buildCommand] to complete the download implementation.
 * Subclasses can also override any method called in [tryDownload] to customize more.
 *
 * Otherwise, considering extending [BaseDownloader] class instead
 *
 * @date 2022-05-14
 * @author CX无敌
 */
abstract class BaseCliDownloader(
  private val downloadConfig: DownloadConfig,
  private val tika: Tika,
  environmentConfig: EnvironmentConfig,
  private val objectMapper: ObjectMapper,
) : BaseDownloader() {

  private val mediainfoLaunchCmd = environmentConfig.mediainfoLaunchCmd

  private val processThreadFactory by lazy { ExternalProcessThreadFactory("$targetPvService-$downloaderName") }

  // we can't refactor out this thread pool because the thread name is tied to the downloader name
  private val threadPool by lazy {
    ThreadPoolExecutor(
      3,
      3,
      downloadConfig.timeout,
      downloadConfig.unit,
      LinkedBlockingDeque(),
      processThreadFactory
    )
  }

  /**
   * download using command line, then use apache tika and mediainfo to identify the file type
   */
  override fun tryDownload(url: String, baseFileName: String, outputDirectory: Path): DownloadFiles {
    // build commands
    val command = buildCommand(url, baseFileName, outputDirectory)

    // execute commands
    log.info { "Executing command: ${command.joinToString(" ", "`", "`")}" }
    execCommand(command)
    log.info { "Done command execution for $baseFileName, collecting downloaded files to their types" }

    // finds the downloaded files, and identify which is PV, audio, thumbnails using apache tika and mediainfo
    log.info { "Start identifying downloaded files for $baseFileName" }
    val downloadFilesToReturn = findDownloadedFiles(outputDirectory, baseFileName)
    log.info { "Done downloading $downloadFilesToReturn" }
    return downloadFilesToReturn
  }

  /**
   * Build the command line used to download the needed resources (pv/audio + thumbnail).
   *
   * The command line will be run under the current directory of this project (so that user can specify the relative path of the executable file).
   *
   * The downloaded files must be stored in [outputDirectory]
   *
   * The downloaded files must contain the string [baseFileName] in their file names (so that we can find these downloaded files).
   *
   * @param baseFileName the base file name of the downloaded files
   * @param outputDirectory the directory where the downloaded files will be stored
   * @param url the url of the pv to be downloaded
   * @return the command line in list of strings that will be used to download the needed resources
   */
  abstract fun buildCommand(url: String, baseFileName: String, outputDirectory: Path): List<String>

  /**
   * Execute the command line in [commands]
   * @param commands List<String> the command line in list of strings to be executed
   */
  protected open fun execCommand(commands: List<String>) {
    runCmd(commands).sync(downloadConfig.timeout, downloadConfig.unit, threadPool) {
      // the order must be stdout first and stderr second, due to how ExternalProcessThreadFactory is coded
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
    processThreadFactory.resetCounter()
  }

  /**
   * Find the downloaded files in [outputDirectory] that contain the string [baseFileName] in their file names.
   * @param outputDirectory Path the directory where the downloaded should be stored, is also where to find these downloaded files
   * @param baseFileName String the base file name of the downloaded files
   * @return DownloadFiles the files that were downloaded and their types
   */
  protected open fun findDownloadedFiles(
    outputDirectory: Path,
    baseFileName: String
  ): DownloadFiles { // open the function for testing
    val downloadedFiles: List<Path> =
      outputDirectory.listDirectoryEntries("*${baseFileName.replace("[", "\\[").replace("]", "\\]")}*")
    val typePathMap = downloadedFiles.associateBy { detectType(it) }
    val pvFile = typePathMap[DownloadFileType.PV]
    val audioFile = typePathMap[DownloadFileType.AUDIO]
    val thumbnailFile = typePathMap[DownloadFileType.THUMBNAIL]
    if (typePathMap.contains(DownloadFileType.OTHERS)) {
      log.warn { "Found other files: ${typePathMap[DownloadFileType.OTHERS]}" }
    }
    if (pvFile == null && audioFile == null) {
      throw RuntimeVocaloidException("None of PV file and audio file is found. Download may failed, or the file renaming is incorrect.")
    }
    checkNotNull(thumbnailFile) { "Thumbnail file is not found" }
    return DownloadFiles(
      pvFile = pvFile,
      audioFile = audioFile,
      thumbnailFile = thumbnailFile,
    )
  }

  /**
   * Detect the type of the [file]
   * @param file Path the file to be detected
   * @return DownloadFileType the type of the file
   */
  protected open fun detectType(file: Path): DownloadFileType { // open the function for testing
    val type = tika.detect(file)
    return when {
      type.contains("video") || type.contains("matroska") -> DownloadFileType.PV // mkv and webp usually get detected as matroska by tika
      type.contains("audio") -> DownloadFileType.AUDIO
      type.contains("image") -> DownloadFileType.THUMBNAIL
      else -> { // perform mediainfo check using command line to identify the file type
        log.debug { "failed to detect using tika on ${file.fileName}" }
        val cmd = buildList {
          addAll(mediainfoLaunchCmd)
          add("--output=JSON")
          add(file.toString())
        }
        val sb = StringBuilder()

        log.debug { "running ${cmd.joinToString(" ", "`", "`")} to detect type" }
        runCmd(cmd).sync(executor = threadPool) {
          onStdOut { sb.append(this.readText()) }
        }
        processThreadFactory.resetCounter()
        val mediaInfoJson = objectMapper.readTree(sb.toString())
        // no need to check if the file exists, since we got the file from `outputDirectory.listDirectoryEntries()`
        val tracks = mediaInfoJson["media"]["track"]
        return if (tracks.size() <= 1) { // confirmed that for image, video and audio files, the first track is always at index 1
          log.warn { "Find out the file ${file.fileName} is not a video or audio or image type" }
          DownloadFileType.OTHERS
        } else {
          when (val typeName = tracks[1]["@type"].asText().lowercase()) {
            "video" -> DownloadFileType.PV
            "audio" -> DownloadFileType.AUDIO
            "image" -> DownloadFileType.THUMBNAIL
            else -> {
              log.warn { "Find out the file ${file.fileName} is a wired type of $typeName" }
              DownloadFileType.OTHERS
            }
          }
        }
      }
    }
  }
  
  @PreDestroy
  fun shutdownThreadPool() {
    log.debug { "Shutting down the common pool in $downloaderName for $targetPvService" }
    threadPool.shutdown()
  }
}

private val log = KInlineLogging.logger()
