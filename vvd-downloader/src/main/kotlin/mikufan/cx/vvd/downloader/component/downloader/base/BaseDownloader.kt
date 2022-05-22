package mikufan.cx.vvd.downloader.component.downloader.base

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vocadbapiclient.model.PVContract
import mikufan.cx.vvd.common.naming.FileNamePostFix
import mikufan.cx.vvd.commonkt.naming.SongProperFileName
import mikufan.cx.vvd.downloader.model.VSongTask
import mikufan.cx.vvd.downloader.util.PVServicesEnum
import mikufan.cx.vvd.downloader.util.renameWithSameExtension
import java.nio.file.Path

/**
 * @date 2021-12-26
 * @author CX无敌
 */

abstract class BaseDownloader {

  /**
   * The exact name of the downloader that is used in the application.yml file,
   * without the pv-service prefix
   */
  abstract val downloaderName: String

  /**
   * targeted PV service
   */
  abstract val targetPvService: PVServicesEnum

  /**
   * Download the pv (or/and audio) and the thumbnail from specific [PVContract] instance to specific directory,
   * given all the information needed from [VSongTask]
   *
   * @param pv the PV to be downloaded
   * @param allInfo all the information about this PV, song, task
   * @param outputDirectory where to save the PV and thumbnail
   * @return Result<DownloadFiles> either a [Result.Failure] with exception or paths of all files successfully downloaded
   * @throws InterruptedException most likely if user presses ctrl+c
   */
  fun download(pv: PVContract, allInfo: VSongTask, outputDirectory: Path): Result<DownloadFiles> {
    val baseFileName = allInfo.parameters.songProperFileName
    return try {
      val url = requireNotNull(pv.url) { "${pv.name} has a null url?" }

      log.info { "Starting downloading files from $url to $outputDirectory with base name $baseFileName" }
      val (pvFile, audioFile, thumbnailFile) = tryDownload(url, baseFileName.toString(), outputDirectory)

      val movedPvFile: Path? = pvFile?.renameWithSameExtension(baseFileName.toPvFileName())
      val movedAudioFile: Path? = audioFile?.renameWithSameExtension(baseFileName.toAudioFileName())
      val movedThumbnailFile = thumbnailFile.renameWithSameExtension(baseFileName.toThumbnailFileName())
      log.info { "Download success =￣ω￣= for $baseFileName" }
      Result.success(DownloadFiles(movedPvFile, movedAudioFile, movedThumbnailFile))
    } catch (e: InterruptedException) {
      Thread.currentThread().interrupt()
      log.error { "Download for $baseFileName gets interrupted" }
      throw e
    } catch (e: Exception) {
      log.error(e) { "Download for $baseFileName failed with error :(" }
      Result.failure(e)
    }
  }

  /**
   * Try download the pv and the thumbnail from the url to specific directory with the common base file name
   *
   * Implementation can throw any exception to represent a failed download.
   * Otherwise, a valid [DownloadFiles] instance should be returned
   *
   * @param url the url of the PV
   * @param baseFileName the base file name that will be included in both thumbnail and pv files.
   * this string is already normalized and safe to be a filename. no other normalization needed.
   * @param outputDirectory Path
   * @return DownloadFiles
   * @throws InterruptedException most likely if user presses ctrl+c
   */
  protected abstract fun tryDownload(url: String, baseFileName: String, outputDirectory: Path): DownloadFiles
}

internal fun SongProperFileName.toPvFileName(extensionWithDot: String = ""): String =
  this.toString() + FileNamePostFix.VIDEO + extensionWithDot

internal fun SongProperFileName.toAudioFileName(extensionWithDot: String = ""): String =
  this.toString() + FileNamePostFix.AUDIO + extensionWithDot

internal fun SongProperFileName.toThumbnailFileName(extensionWithDot: String = ""): String =
  this.toString() + FileNamePostFix.THUMBNAIL + extensionWithDot

private val log = KInlineLogging.logger()
