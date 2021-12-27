package mikufan.cx.vvd.downloader.component.downloader

import mikufan.cx.vocadbapiclient.model.PVContract
import mikufan.cx.vvd.common.naming.FileNamePostFix
import mikufan.cx.vvd.commonkt.naming.SongProperFileName
import mikufan.cx.vvd.downloader.model.VSongTask
import mikufan.cx.vvd.downloader.util.PVServicesEnum
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.moveTo

/**
 * @date 2021-12-26
 * @author CX无敌
 */

interface BaseDownloader {

  /**
   * The exact name of the downloader that is used in the application.yml file,
   * without the pv-service prefix
   */
  val downloaderName: String

  /**
   * targeted PV service
   */
  val targetPvService: PVServicesEnum

  /**
   * Download the pv and the thumbnail from specific [PVContract] instance to specific directory, given all the information needed from [VSongTask]
   *
   * @param pv the PV to be downloaded
   * @param allInfo all the information about this PV, song, task
   * @param outputDirectory where to save the PV and thumbnail
   * @return Result<DownloadFiles> either a [Result.Failure] with exception or paths of all files successfully downloaded
   * @throws InterruptedException most likely if user presses ctrl+c
   */
  fun downloadPvAndThumbnail(pv: PVContract, allInfo: VSongTask, outputDirectory: Path): Result<DownloadFiles> {
    return try {
      val baseFileName = allInfo.parameters.songProperFileName
      val url = requireNotNull(pv.url) { "${pv.name} has a null url?" }

      val (pvFile, thumbnailFile) = tryDownload(url, baseFileName.toString(), outputDirectory)

      val movedPvFile = pvFile.renameWithSameExtension(baseFileName.toPvFileName())
      val movedThumbnailFile = thumbnailFile.renameWithSameExtension(baseFileName.toThumbnailFileName())
      Result.success(DownloadFiles(movedPvFile, movedThumbnailFile))
    } catch (e: InterruptedException) {
      Thread.currentThread().interrupt()
      throw e
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  /**
   * This method should not be called outside of [downloadPvAndThumbnail]
   *
   * Try download the pv and the thumbnail from the url to specific directory with the common base file name
   *
   * Implementation can throw any exception to represent a failed download.
   * Otherwise, a [DownloadFiles] instance with all valid files should be returned
   *
   * @param url the url of the PV
   * @param baseFileName the base file name that will be included in both thumbnail and pv files
   * @param outputDirectory Path
   * @return DownloadFiles
   * @throws InterruptedException most likely if user presses ctrl+c
   */
  fun tryDownload(url: String, baseFileName: String, outputDirectory: Path): DownloadFiles
}

data class DownloadFiles(
  val pvFile: Path,
  val thumbnailFile: Path
)

private fun Path.renameWithSameExtension(newFileNameWithoutDot: String): Path {
  val sibling = this.resolveSibling(newFileNameWithoutDot + this.extension)
  this.moveTo(sibling, overwrite = true)
  return sibling
}

internal fun SongProperFileName.toPvFileName(extensionWithDot: String = ""): String {
  return this.toString() + FileNamePostFix.VIDEO + extensionWithDot
}

internal fun SongProperFileName.toThumbnailFileName(extensionWithDot: String = ""): String {
  return this.toString() + FileNamePostFix.THUMBNAIL + extensionWithDot
}
