package mikufan.cx.vvd.taskproducer.component

import mikufan.cx.vvd.common.label.ValidationPhase
import mikufan.cx.vvd.commonkt.batch.CustomizableBeanRecordValidator
import mikufan.cx.vvd.taskproducer.model.VSongTask
import mikufan.cx.vvd.taskproducer.util.toInfoFileName
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import javax.validation.Validator

/**
 * @date 2021-06-12
 * @author CX无敌
 */
@Component
@Order(2)
class LabelInfoRecorder : RecordProcessor<VSongTask, VSongTask> {
  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    val song = requireNotNull(record.payload.parameters.songForApiContract) { "VSong is null" }
    return record.apply {
      payload.label.infoFileName = song.toInfoFileName()
      payload.label.order = record.header.number
    }
  }
}

/**
 * @date 2021-06-12
 * @author CX无敌
 */
@Component
@Order(3)
class BeforeWriteValidator(
  validator: Validator
) : CustomizableBeanRecordValidator<VSongTask>(validator, ValidationPhase.One::class.java) {
  override fun Record<VSongTask>.toValidationObject(): Any {
    return payload.label
  }
}
