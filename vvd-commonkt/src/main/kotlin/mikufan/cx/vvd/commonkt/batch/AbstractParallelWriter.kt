package mikufan.cx.vvd.commonkt.batch

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jeasy.batch.core.record.Batch
import org.jeasy.batch.core.writer.RecordWriter

/**
 * A basic record writer that use coroutine to write records in parallel
 *
 * by default, any exception thrown will be rethrow and interrupt the whole writing process,
 * passing [RecordErrorHandler] to override this behavior
 *
 * @date 2021-06-14
 * @author CX无敌
 */
abstract class AbstractParallelWriter<P>(
  private val recordErrorHandler: RecordErrorHandler? = null
) : RecordWriter<P> {
  override fun writeRecords(batch: Batch<P>) = runBlocking(Dispatchers.IO) {
    batch.forEach {
      launch {
        try {
          write(it.payload)
        } catch (e: Exception) {
          recordErrorHandler?.writeError(it, e) ?: throw e
        }
      }
    }
  }

  abstract suspend fun write(payload: P)
}
