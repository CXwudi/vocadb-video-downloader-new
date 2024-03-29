package mikufan.cx.vvd.taskproducer.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withContext
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.commonkt.batch.LOOM
import mikufan.cx.vvd.commonkt.batch.RecordErrorWriter
import mikufan.cx.vvd.commonkt.batch.toIterator
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

  /**
   * A semaphore to control the number of concurrent tasks
   *
   * We still need this because we don't want to store all the tasks in memory
   */
  private val semaphore = Semaphore(threadLimit)

  override fun run() = runBlocking { // only one thread env
    withContext(Dispatchers.LOOM) {
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
    // at this time, only song info fetched, but label json is not ready yet
    // also not using toProperSongName as the artist string is still unfixed
    val songName = record.payload.parameters.songForApiContract?.defaultName
    log.info { "Start processing $songName" }
    var currentRecord: Record<Any> = record as Record<Any>
    try {
      taskProcessors.forEach { recordProcessor ->
        currentRecord = (recordProcessor as RecordProcessor<Any, Any>).processRecord(currentRecord)
      }
      labelSaver.write(currentRecord as Record<VSongTask>)
      log.info { "Done processing $songName" }
    } catch (e: InterruptedException) {
      Thread.currentThread().interrupt()
      log.error { "Task producer process interrupted by the user, quiting" }
      throw e
    } catch (e: Exception) {
      log.error(e) { "An exception occurred when processing ${songName ?: "an unknown song"}, check the error directory for more information" }
      recordErrorWriter.handleError(currentRecord, e)
    } finally {
      semaphore.release() // release the semaphore when done to allow next task continues
    }
  }
}

private val log = KInlineLogging.logger()
