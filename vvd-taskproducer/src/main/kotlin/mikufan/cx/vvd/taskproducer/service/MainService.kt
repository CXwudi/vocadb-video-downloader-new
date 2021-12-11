package mikufan.cx.vvd.taskproducer.service

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.commonkt.batch.RecordErrorWriter
import mikufan.cx.vvd.commonkt.batch.toIterator
import mikufan.cx.vvd.taskproducer.component.ListReader
import mikufan.cx.vvd.taskproducer.component.VSongJsonWriter
import mikufan.cx.vvd.taskproducer.config.SystemConfig
import mikufan.cx.vvd.taskproducer.model.VSongTask
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.stereotype.Service
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

@Service
class MainService(
  private val listReader: ListReader,
  private val taskProcessors: List<RecordProcessor<*, *>>,
  private val vSongJsonWriter: VSongJsonWriter,
  private val recordErrorWriter: RecordErrorWriter,
  systemConfig: SystemConfig
) : Runnable {

  private val threadLimit = systemConfig.batchSize

  private val executor = ThreadPoolExecutor(
    threadLimit, threadLimit,
    1L, TimeUnit.HOURS,
    LinkedBlockingDeque()
  )

  private val dispatcher = executor.asCoroutineDispatcher()

  override fun run() = runBlocking(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) { // only one thread env
    coroutineScope {
      for (record in listReader.toIterator()) {
        // launch the job in the controlled executor
        launch(dispatcher) { processRecord(record) }
        while (executor.activeCount >= threadLimit) {
          /* don't call readRecord() until concurrent active amount get lower */
        }
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
