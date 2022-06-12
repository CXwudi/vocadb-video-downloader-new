package mikufan.cx.vvd.downloader.component

import mikufan.cx.vvd.downloader.model.VSongTask
import mikufan.cx.vvd.downloader.util.OrderConstants.DOWNLOAD_RESULT_RECORDER_ORDER
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

/**
 * @date 2022-06-11
 * @author CX无敌
 */
@Component
@Order(DOWNLOAD_RESULT_RECORDER_ORDER)
class DownloadResultRecorder : RecordProcessor<VSongTask, VSongTask> {

  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    val vSongTask = record.payload
    val downloadFiles = requireNotNull(vSongTask.parameters.downloadFiles) { " null download files?" }
    val downloadedPv = requireNotNull(vSongTask.parameters.downloadedPv) { " null download pv?" }
    vSongTask.label.apply {
      downloadFiles.pvFile?.let {
        this.pvFileName = it.fileName.toString()
      }
      downloadFiles.audioFile?.let {
        this.audioFileName = it.fileName.toString()
      }
      this.thumbnailFileName = downloadFiles.thumbnailFile.fileName.toString()
      // recording of PV information to label is done here because only here we know the PV that is successfully downloaded
      this.pvId = downloadedPv.pvId
      this.pvService = downloadedPv.service.toString()
      this.pvUrl = downloadedPv.url
    }
    return record
  }
}
