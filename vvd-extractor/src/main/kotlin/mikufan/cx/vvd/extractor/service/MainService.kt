package mikufan.cx.vvd.extractor.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.commonkt.batch.RecordErrorWriter
import mikufan.cx.vvd.commonkt.batch.toIterator
import mikufan.cx.vvd.extractor.component.LabelsReader
import mikufan.cx.vvd.extractor.config.BatchConfig
import mikufan.cx.vvd.extractor.model.VSongTask
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.stereotype.Service

/**
 * @date 2022-06-11
 * @author CX无敌
 */
@Service
class MainService(
  private val labelsReader: LabelsReader,
  private val taskProcessors: List<RecordProcessor<*, *>>,
  private val recordErrorWriter: RecordErrorWriter,
  batchConfig: BatchConfig
) : Runnable {

  private val semaphore = Semaphore(batchConfig.batchSize)

  override fun run() {
    runBlocking(Dispatchers.IO) {
      semaphore.acquire()
      labelsReader.toIterator().forEach { label ->
        launch {
          processExtract(label)
        }
        semaphore.acquire()
      }
    }
    TODO("new function to save all data sequentially, with respect to their orders")
    log.info { "やった！全部完成" }
  }

  @Suppress("UNCHECKED_CAST")
  private suspend fun processExtract(record: Record<VSongTask>) {
    val infoFileName = record.payload.label.infoFileName // song info is not loaded at this time
    var currentRecord: Record<Any> = record as Record<Any>
    try {
      taskProcessors.forEach { recordProcessor ->
        currentRecord = (recordProcessor as RecordProcessor<Any, Any>).processRecord(currentRecord)
      }
      // TODO: save the label
    } catch (e: Exception) {
      log.error(e) { "An exception occurred when processing ${infoFileName ?: "an unknown song"}, check the error directory for more information" }
      recordErrorWriter.handleError(currentRecord, e)
    } finally {
      semaphore.release() // release the semaphore when done to allow next task continues
    }
  }
}

private val log = KInlineLogging.logger()
