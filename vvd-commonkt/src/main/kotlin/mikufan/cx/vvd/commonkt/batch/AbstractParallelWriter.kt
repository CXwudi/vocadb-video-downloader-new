package mikufan.cx.vvd.commonkt.batch

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jeasy.batch.core.record.Batch
import org.jeasy.batch.core.record.Record
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
  private val recordErrorHandler: RecordErrorHandler? = null,
  private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : RecordWriter<P> {
  override fun writeRecords(batch: Batch<P>) = runBlocking(dispatcher) {
    batch.forEach {
      launch { // let the main coroutine free from running on IO
        try {
          write(it)
        } catch (e: Exception) {
          recordErrorHandler?.handleError(it, e) ?: throw e
        }
      }
    }
  }

  /**
   * write a single record to somewhere.
   *
   * blocking IO is fine here as this suspend fun is ran in [Dispatchers.IO]
   */
  abstract suspend fun write(record: Record<P>)
}
