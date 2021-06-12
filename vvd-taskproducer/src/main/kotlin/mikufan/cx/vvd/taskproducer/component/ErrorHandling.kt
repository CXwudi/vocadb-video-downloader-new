package mikufan.cx.vvd.taskproducer.component

import com.fasterxml.jackson.databind.ObjectMapper
import mikufan.cx.vvd.commonkt.label.FailedObject
import mikufan.cx.vvd.commonkt.naming.toErrorFileName
import mikufan.cx.vvd.taskproducer.config.IOConfig
import mikufan.cx.vvd.taskproducer.model.VSongTask
import org.jeasy.batch.core.listener.PipelineListener
import org.jeasy.batch.core.record.Record
import org.springframework.stereotype.Component

/**
 * @date 2021-06-12
 * @author CX无敌
 */
@Component
class PipelineExceptionHandler(
  private val recordErrorWriter: RecordErrorWriter
) : PipelineListener {
  override fun <P : Any?> onRecordProcessingException(record: Record<P>, throwable: Throwable) = when (throwable) {
    is Error, is InterruptedException -> throw throwable // rethrow error or interrupted exception
    else -> recordErrorWriter.writeError(record, throwable as Exception)
  }
}

/**
 * @date 2021-06-11
 * @author CX无敌
 */
@Component
class RecordErrorWriter(
  ioConfig: IOConfig,
  private val objectMapper: ObjectMapper
) {

  private val outputDirectory = ioConfig.outputDirectory

  fun writeError(record: Record<*>, e: Exception) {
    val failureFileName = when (record.payload) {
      is VSongTask -> {
        val record1 = record as Record<VSongTask>
        record1.payload.parameters.songForApiContract?.toErrorFileName() ?: "unknown song ${record1.header}.json"
      }
      // WARNING: if we have any other types of Record<> then we need to add custom error handling code here
      else -> {
        "failure record ${record.header}.json"
      }
    }
    val failureFile = outputDirectory.resolve(failureFileName)
    objectMapper.writeValue(failureFile.toFile(), FailedObject(record, e))
  }

}
