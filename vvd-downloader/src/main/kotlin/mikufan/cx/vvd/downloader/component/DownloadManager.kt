package mikufan.cx.vvd.downloader.component

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.downloader.component.downloader.EnabledDownloaders
import mikufan.cx.vvd.downloader.config.IOConfig
import mikufan.cx.vvd.downloader.config.preference.Preference
import mikufan.cx.vvd.downloader.model.VSongTask
import mikufan.cx.vvd.downloader.util.OrderConstants
import mikufan.cx.vvd.downloader.util.toPVServicesEnum
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

/**
 * @date 2021-12-10
 * @author CX无敌
 */
@Component
@Order(OrderConstants.DOWNLOAD_MANAGER_ORDER)
class DownloadManager(
  private val enabledDownloaders: EnabledDownloaders,
  ioConfig: IOConfig,
  preference: Preference,
) : RecordProcessor<VSongTask, VSongTask> {

  private val outputDirectory = ioConfig.outputDirectory
  private val attempt = preference.maxRetryCount

  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    val failures = mutableListOf<Exception>()
    val vSongTask = record.payload
    val pvs = requireNotNull(record.payload.parameters.pvCandidates) { "null pv candidate before download?" }

    log.info { "Start downloading attempts on ${vSongTask.parameters.songProperFileName}" }
    for (pv in pvs) {
      val downloaders = enabledDownloaders.getDownloaderForPvService(requireNotNull(pv.service?.toPVServicesEnum()))
      for (downloader in downloaders) {
        for (attempt in 1..(1 + attempt)) {
          log.debug { "    on attempt $attempt on pv ${pv.url} with downloader ${downloader.downloaderName}" }
          val result = downloader.download(pv, vSongTask, outputDirectory)
          result.fold(
            onSuccess = { downloadFiles ->
              log.info { "Download attempt on ${vSongTask.parameters.songProperFileName} success" }
              vSongTask.label.apply {
                downloadFiles.pvFile?.let {
                  this.pvFileName = it.fileName.toString()
                }
                downloadFiles.audioFile?.let {
                  this.audioFileName = it.fileName.toString()
                }
                this.thumbnailFileName = downloadFiles.thumbnailFile.fileName.toString()
                // recording of PV information to label is done here because only here we know the PV that is successfully downloaded
                this.pvId = pv.pvId
                this.pvService = pv.service.toString()
                this.pvUrl = pv.url
              }
              return record
            },
            onFailure = {
              log.warn {
                "    Attempt $attempt on pv ${pv.url} " +
                    "with downloader ${downloader.downloaderName} failed, trying again"
              }
              failures.add(it as Exception)
            }
          )
        }
        log.warn { "  Downloader ${downloader.downloaderName} failed to download pv ${pv.url}, trying next downloader" }
      }
      log.warn { "All attempts on pv ${pv.url} failed, trying next PV" }
    }
    log.error { "Failed to download any resources on ${vSongTask.parameters.songProperFileName}" }
    throw RuntimeVocaloidException(
      "Failed to download any resources on ${vSongTask.parameters.songProperFileName} with any possible attempts, exception list: " +
          failures.joinToString(prefix = "[", postfix = "]")
    )
  }
}

private val log = KInlineLogging.logger()
