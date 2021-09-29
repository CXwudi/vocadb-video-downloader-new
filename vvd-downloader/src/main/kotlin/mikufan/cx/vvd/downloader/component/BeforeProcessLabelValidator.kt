package mikufan.cx.vvd.downloader.component

import mikufan.cx.vvd.common.label.ValidationPhase
import mikufan.cx.vvd.commonkt.batch.CustomizableBeanRecordValidator
import mikufan.cx.vvd.downloader.model.VSongTask
import org.jeasy.batch.core.record.Record
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import javax.validation.Validator

@Component
@Order(1)
class BeforeProcessLabelValidator(
  validator: Validator
) : CustomizableBeanRecordValidator<VSongTask>(validator, ValidationPhase.One::class.java) {
  override fun Record<VSongTask>.toValidationObject() = this.payload.label
}
