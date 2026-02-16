package mikufan.cx.vvd.commonkt.batch

import tools.jackson.databind.ObjectMapper
import mikufan.cx.vvd.commonkt.label.FailedObject
import mikufan.cx.vvd.commonkt.naming.removeIllegalChars
import org.jeasy.batch.core.listener.PipelineListener
import org.jeasy.batch.core.record.Record
import org.springframework.stereotype.Component
import java.nio.file.Path

/**
 * An wrapper of [RecordErrorHandler]
 * @date 2021-06-12
 * @author CX无敌
 */
@Component
class PipelineExceptionHandler(
  private val recordErrorHandler: RecordErrorHandler
) : PipelineListener {
  override fun <P : Any?> onRecordProcessingException(record: Record<P>, throwable: Throwable) = when (throwable) {
    is Error, is InterruptedException -> throw throwable // rethrow error or interrupted exception
    else -> recordErrorHandler.handleError(record, throwable as Exception)
  }
}

/**
 * Our internal base interface to handle any record with exception happened
 *
 * Can be used anywhere for handling error record
 */
@FunctionalInterface
interface RecordErrorHandler {
  fun handleError(record: Record<*>, e: Exception)
}


/**
 * To write easy-batch record into a file, with customizable filename
 * @date 2021-06-11
 * @author CX无敌
 */
class RecordErrorWriter(
  private val errorDirectory: Path,
  private val objectMapper: ObjectMapper,
  private val errorFileNaming: (Record<*>) -> String = { "" }
) : RecordErrorHandler {

  override fun handleError(record: Record<*>, e: Exception) {
    val failureFileName = errorFileNaming(record).ifBlank {
      "failure record ${removeIllegalChars(record.header.toString())}.json"
    }
    val failureFile = errorDirectory.resolve(failureFileName)
    objectMapper.writeValue(failureFile.toFile(), FailedObject(record, e))
  }
}

