package mikufan.cx.vvd.taskproducer.service

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.commonkt.batch.RecordErrorWriter
import mikufan.cx.vvd.taskproducer.component.ListReader
import mikufan.cx.vvd.taskproducer.component.VSongJsonWriter
import mikufan.cx.vvd.taskproducer.config.SystemConfig
import mikufan.cx.vvd.taskproducer.model.VSongTask
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.stereotype.Service
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

@Service
class MainService(
  private val listReader: ListReader,
  private val taskProcessors: List<RecordProcessor<*, *>>,
  private val vSongJsonWriter: VSongJsonWriter,
  private val recordErrorWriter: RecordErrorWriter,
  private val systemConfig: SystemConfig
) : Runnable {
  private val executor = ThreadPoolExecutor(
    systemConfig.batchSize, systemConfig.batchSize,
    1L, TimeUnit.HOURS,
    LinkedBlockingDeque()
  )

  override fun run() = runBlocking(executor.asCoroutineDispatcher()) {
    coroutineScope {
      lateinit var record: Record<VSongTask>
      while (listReader.readRecord()?.also { record = it } != null) {
        val thisRecord = record
        while (executor.activeCount >= systemConfig.batchSize) {
          /* wait until concurrent active amount get lower */
          // this part of the code has a bug, if batch size = 1, deadlock happens here
          // maybe because only one thread in executor and we already have main function as one thread
          // if set batch size = 2, we have the main thread reading records, and another thread processing the record
        }
        launch { processRecord(thisRecord) }
      }
    } // until all tasks finished
    log.info { "やった！続きはPVをダウンロードするに行くぞ" }
  }

  @Suppress("UNCHECKED_CAST")
  private suspend fun processRecord(record: Record<VSongTask>) {
    var currentRecord: Record<Any> = record as Record<Any>
    try {
      taskProcessors.forEach { recordProcessor ->
        currentRecord = (recordProcessor as RecordProcessor<Any, Any>).processRecord(currentRecord)
      }
      vSongJsonWriter.write(currentRecord as Record<VSongTask>)
    } catch (e: Exception) {
      recordErrorWriter.handleError(currentRecord, e)
    }
  }
}

private val log = KInlineLogging.logger()
