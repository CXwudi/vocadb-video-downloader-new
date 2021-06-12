package mikufan.cx.vvd.taskproducer.component

import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import mikufan.cx.vvd.common.label.ValidationPhase
import mikufan.cx.vvd.commonkt.exception.orThrowVocaloidExp
import mikufan.cx.vvd.taskproducer.model.VSongTask
import mikufan.cx.vvd.taskproducer.util.toInfoFileName
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.jeasy.batch.core.validator.RecordValidator
import org.springframework.stereotype.Component
import javax.validation.Validator

/**
 * @date 2021-06-12
 * @author CX无敌
 */
@Component
class VSongFileNameGenerator : RecordProcessor<VSongTask, VSongTask> {
  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    val song = record.payload.parameters.songForApiContract.orThrowVocaloidExp("VSong is null")
    val label = record.payload.label
    label.infoFileName = song.toInfoFileName()
    return record
  }
}

/**
 * @date 2021-06-12
 * @author CX无敌
 */
@Component
class BeforeWriteValidator(
  private val validator: Validator
) : RecordValidator<VSongTask> {
  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    val label = record.payload.label
    val result = validator.validate(label, ValidationPhase.One::class.java)
    if (result.isNotEmpty()) {
      throw RuntimeVocaloidException("Validation of label info failed: $result")
    }
    return record
  }
}