package mikufan.cx.vvd.extractor.component

import jakarta.validation.Validator
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.common.label.ValidationPhase
import mikufan.cx.vvd.commonkt.batch.CustomizableBeanRecordValidator
import mikufan.cx.vvd.extractor.model.VSongTask
import mikufan.cx.vvd.extractor.util.OrderConstants
import org.jeasy.batch.core.record.Record
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

/**
 * @date 2022-06-02
 * @author CX无敌
 */
@Component
@Order(OrderConstants.BEFORE_SAVE_LABEL_VALIDATOR_ORDER)
class BeforeSaveLabelValidator(
  validator: Validator,
) : CustomizableBeanRecordValidator<VSongTask>(validator, ValidationPhase.Three::class.java) {

  override fun processRecord(record: Record<VSongTask>): Record<VSongTask> {
    log.info { "Validate label info before saving: ${record.payload.label.labelFileName}" }
    return super.processRecord(record)
  }

  override fun Record<VSongTask>.toValidationObject() = this.payload.label
}

private val log = KInlineLogging.logger()
