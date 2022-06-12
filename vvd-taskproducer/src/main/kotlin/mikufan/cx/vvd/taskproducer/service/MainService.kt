package mikufan.cx.vvd.taskproducer.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withContext
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.commonkt.batch.RecordErrorWriter
import mikufan.cx.vvd.commonkt.batch.toIterator
import mikufan.cx.vvd.commonkt.naming.toProperFileName
import mikufan.cx.vvd.taskproducer.component.LabelSaver
import mikufan.cx.vvd.taskproducer.component.ListReader
import mikufan.cx.vvd.taskproducer.config.SystemConfig
import mikufan.cx.vvd.taskproducer.model.VSongTask
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.stereotype.Service

@Service
class MainService(
  private val listReader: ListReader,
  private val taskProcessors: List<RecordProcessor<*, *>>,
  private val labelSaver: LabelSaver,
  private val recordErrorWriter: RecordErrorWriter,
  systemConfig: SystemConfig
) : Runnable {

  private val threadLimit = systemConfig.batchSize

  private val semaphore = Semaphore(threadLimit) // only allow threadLimit amount of thread to run concurrently

  override fun run() = runBlocking { // only one thread env
    withContext(Dispatchers.IO) {
      semaphore.acquire() // thread limit applies even before reading the list
      for (record in listReader.toIterator()) {
        launch { processRecord(record) }
        semaphore.acquire() // don't read the list immediately after this iteration without thread limit
      }
    } // until all tasks finished
    log.info { "やった！続きはPVをダウンロードするに行くぞ" }
  }

  @Suppress("UNCHECKED_CAST")
  private suspend fun processRecord(record: Record<VSongTask>) {
    val songName = record.payload.parameters.songForApiContract?.toProperFileName()
    log.info { "Start processing $songName" }
    var currentRecord: Record<Any> = record as Record<Any>
    try {
      taskProcessors.forEach { recordProcessor ->
        currentRecord = (recordProcessor as RecordProcessor<Any, Any>).processRecord(currentRecord)
      }
      labelSaver.write(currentRecord as Record<VSongTask>)
      log.info { "Done processing $songName" }
    } catch (e: Exception) {
      recordErrorWriter.handleError(currentRecord, e)
    } finally {
      semaphore.release() // release the semaphore when done to allow next task continues
    }
  }
}

private val log = KInlineLogging.logger()
