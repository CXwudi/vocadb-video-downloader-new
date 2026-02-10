package mikufan.cx.vvd.downloader.component

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.downloader.component.downloader.EnabledDownloaders
import mikufan.cx.vvd.downloader.config.IOConfig
import mikufan.cx.vvd.downloader.config.preference.Preference
import mikufan.cx.vvd.downloader.model.VSongTask
import mikufan.cx.vvd.downloader.util.OrderConstants
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

/**
 * Manager of downloaders, handles retrying, switching to next downloader, switching to next pv and etc.
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
    val songProperFileName = vSongTask.parameters.songProperFileName

    log.info { "Start downloading attempts on $songProperFileName" }
    for (pv in pvs) {
      val downloaders = enabledDownloaders.getDownloaderForPvService(
        requireNotNull(pv.service) { "the pv service enum is null for ${pv.name}?" }
      )
      for (downloader in downloaders) {
        for (attempt in 1..(1 + attempt)) {
          log.debug { "    on attempt $attempt on pv ${pv.url} with downloader ${downloader.downloaderName}" }
          val result = downloader.download(pv, vSongTask, outputDirectory)
          result.fold(
            onSuccess = { downloadFiles ->
              log.info { "Download attempt on $songProperFileName success" }
              vSongTask.parameters.apply {
                this.downloadFiles = downloadFiles
                this.downloadedPv = pv
                this.downloaderName = downloader.downloaderName
              }
              return record
            },
            onFailure = {
              if (it !is Exception) { throw it }
              log.warn {
                "    Attempt $attempt on pv ${pv.url} " +
                    "with downloader ${downloader.downloaderName} failed, trying again"
              }
              failures.add(it)
            }
          )
        }
        log.warn { "  Downloader ${downloader.downloaderName} failed to download pv ${pv.url}, trying next downloader" }
      }
      log.warn { "All attempts on pv ${pv.url} failed, trying next PV" }
    }
    log.error { "Failed to download any resources on $songProperFileName" }
    throw RuntimeVocaloidException(
      "Failed to download any resources on $songProperFileName with any possible attempts, exception list: " +
          failures.joinToString(prefix = "[", postfix = "]")
    )
  }
}

private val log = KInlineLogging.logger()
