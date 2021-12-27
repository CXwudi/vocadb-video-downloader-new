package mikufan.cx.vvd.downloader.component.downloader

import mikufan.cx.vocadbapiclient.model.PVContract
import mikufan.cx.vvd.downloader.model.VSongTask
import mikufan.cx.vvd.downloader.util.PVServicesEnum
import java.nio.file.Path

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
      Result.success(tryDownload(pv, allInfo, outputDirectory))
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
   * Try download the pv and the thumbnail from specific [PVContract] instance to specific directory, given all the information needed from [VSongTask]
   * Implementation can throw any exception to represent a failed download.
   * Otherwise, a [DownloadFiles] instance with all valid files should be returned
   *
   * @param pv PVContract
   * @param allInfo VSongTask
   * @param outputDirectory Path
   * @return
   * @throws InterruptedException most likely if user presses ctrl+c
   */
  fun tryDownload(pv: PVContract, allInfo: VSongTask, outputDirectory: Path): DownloadFiles
}

data class DownloadFiles(
  val pvFile: Path,
  val thumbnailFile: Path
)
