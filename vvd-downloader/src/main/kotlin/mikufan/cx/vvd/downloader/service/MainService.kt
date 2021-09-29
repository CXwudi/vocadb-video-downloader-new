package mikufan.cx.vvd.downloader.service

import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.downloader.component.LabelsReader
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.stereotype.Service

/**
 * @date 2021-08-30
 * @author CX无敌
 */
@Service
class MainService(
  val labelsReader: LabelsReader,
  val processors: List<RecordProcessor<*, *>>,
) : Runnable {

  override fun run() { // not allow parallelism to avoid IP banned from downloading
    // this is fixed to 1 to allow label and info files moved and saved immediately after the downloading

//      .batchSize(1)
//      .reader(labelsReader)
//      .validator(beforeProcessLabelValidator)
//      .mapper(labelToTaskMapper)
    lateinit var label: Record<VSongLabel>
    while (labelsReader.readRecord()?.also { label = it } != null) {
      val thisLabel = label
      processDownload(thisLabel)
    }
  }

  @Suppress("UNCHECKED_CAST")
  private fun processDownload(label: Record<VSongLabel>) {
    var current: Record<Any> = label as Record<Any>
    try {
      for (processor in processors) {
        current = (processor as RecordProcessor<Any, Any>).processRecord(current)
      }
    } catch (e: Exception) {
      // TODO: write error
    }
  }
}

private val log = KInlineLogging.logger()
