package mikufan.cx.vvd.taskproducer.component

import jakarta.validation.Validator
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.common.label.ValidationPhase
import mikufan.cx.vvd.commonkt.batch.CustomizableBeanRecordValidator
import mikufan.cx.vvd.commonkt.naming.toProperFileName
import mikufan.cx.vvd.taskproducer.model.VSongTask
import mikufan.cx.vvd.taskproducer.util.OrderConstants
import mikufan.cx.vvd.taskproducer.util.toInfoFileName
import mikufan.cx.vvd.taskproducer.util.toLabelFileName
import org.jeasy.batch.core.processor.RecordProcessor
import org.jeasy.batch.core.record.Record
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

/**
 * @date 2021-06-12
 * @author CX无敌
 */
@Component
@Order(OrderConstants.LABEL_INFO_RECORDER_ORDER)
class LabelInfoRecorder : RecordProcessor<VSongTask, VSongTask> {
  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    val song = requireNotNull(record.payload.parameters.songForApiContract) { "VSong is null" }
    log.info { "Done processing, creating label info for ${song.defaultName}" }
    val properFileName = song.toProperFileName()
    return record.apply {
      payload.label.labelFileName = properFileName.toLabelFileName()
      payload.label.infoFileName = properFileName.toInfoFileName()
      // order is already recorded during list reader
    }
  }
}

/**
 * @date 2021-06-12
 * @author CX无敌
 */
@Component
@Order(OrderConstants.BEFORE_WRITE_VALIDATOR_ORDER)
class BeforeWriteValidator(
  validator: Validator
) : CustomizableBeanRecordValidator<VSongTask>(validator, ValidationPhase.One::class.java) {
  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    log.info { "Validate label info after processing, before write: ${record.payload.label.labelFileName}" }
    return super.processRecord(record)
  }

  override fun Record<VSongTask>.toValidationObject(): Any {
    return payload.label
  }
}

private val log = KInlineLogging.logger()
