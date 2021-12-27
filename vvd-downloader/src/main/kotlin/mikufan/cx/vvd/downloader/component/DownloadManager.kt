package mikufan.cx.vvd.downloader.component

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.downloader.component.downloader.EnabledDownloaders
import mikufan.cx.vvd.downloader.config.IOConfig
import mikufan.cx.vvd.downloader.config.Preference
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
  private val errorDirectory = ioConfig.errorDirectory
  private val attemp = preference.maxRetryCount

  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    val failures = mutableListOf<Exception>()
    val vSongTask = record.payload
    val pvs = requireNotNull(record.payload.parameters.pvCandidates) { "null pv candidate before download?" }

    for (pv in pvs) {
      val downloaders = enabledDownloaders.getDownloaderForPvService(requireNotNull(pv.service?.toPVServicesEnum()))
      for (downloader in downloaders) {
        for (attemp in 1..(1 + attemp)) {
          val result = downloader.downloadPvAndThumbnail(pv, vSongTask, outputDirectory)
          TODO("handle result")
        }
      }
    }
    TODO()
  }
}

private val log = KInlineLogging.logger()
