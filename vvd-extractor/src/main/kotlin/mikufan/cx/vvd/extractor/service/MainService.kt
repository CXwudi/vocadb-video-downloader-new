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
import java.util.*

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
    // this map can potentially be large as it eventually saves all song info and label info
    val processed = Collections.synchronizedMap(mutableMapOf<Int, VSongTask>())
    runBlocking(Dispatchers.IO) {
      semaphore.acquire()
      labelsReader.toIterator().withIndex().forEach { (index, label) ->
        launch {
          processExtract(label).onSuccess {
            processed[index] = it.payload
          }
        }
        semaphore.acquire()
      }
    }
    TODO("new function to save all data sequentially, with respect to their orders")
    log.info { "やった！全部完成" }
  }

  @Suppress("UNCHECKED_CAST")
  private suspend fun processExtract(label: Record<VSongTask>): Result<Record<VSongTask>> {
    var currentRecord: Record<Any> = label as Record<Any>
    return try {
      taskProcessors.forEach { recordProcessor ->
        currentRecord = (recordProcessor as RecordProcessor<Any, Any>).processRecord(currentRecord)
      }
      Result.success(currentRecord as Record<VSongTask>)
    } catch (e: Exception) {
      recordErrorWriter.handleError(currentRecord, e)
      Result.failure(e)
    } finally {
      semaphore.release() // release the semaphore when done to allow next task continues
    }
  }
}

private val log = KInlineLogging.logger()
