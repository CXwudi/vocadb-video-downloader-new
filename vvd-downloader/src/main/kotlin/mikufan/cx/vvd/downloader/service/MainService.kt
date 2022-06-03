package mikufan.cx.vvd.downloader.service

import kotlinx.coroutines.runBlocking
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.commonkt.batch.RecordErrorWriter
import mikufan.cx.vvd.commonkt.batch.toIterator
import mikufan.cx.vvd.downloader.component.LabelSaver
import mikufan.cx.vvd.downloader.component.LabelsReader
import mikufan.cx.vvd.downloader.model.VSongTask
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.stereotype.Service

/**
 * @date 2021-08-30
 * @author CX无敌
 */
@Service
class MainService(
  private val labelsReader: LabelsReader,
  private val processors: List<RecordProcessor<*, *>>,
  private val labelSaver: LabelSaver,
  private val errorWriter: RecordErrorWriter
) : Runnable {

  override fun run() { // not allow parallelism to avoid IP banned from downloading
    for (label in labelsReader.toIterator()) {
      processDownload(label)
    }
    log.info { "やった！PVのダウンロードを全部完成した。続きは音楽を抽出するに行くぞ o(*￣▽￣*)ブ" }
  }

  @Suppress("UNCHECKED_CAST")
  // current we don't need to cast to Any, but for better robustness, let's keep it for now
  private fun processDownload(label: Record<VSongTask>) {
    var current: Record<Any> = label as Record<Any>
    try {
      for (processor in processors) {
        current = (processor as RecordProcessor<Any, Any>).processRecord(current)
      }
      runBlocking { labelSaver.write(current as Record<VSongTask>) }
    } catch (e: Exception) {
      errorWriter.handleError(current, e)
    }
  }
}

private val log = KInlineLogging.logger()
