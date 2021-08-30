package mikufan.cx.vvd.downloader.component

import mikufan.cx.vvd.common.label.VSongLabel
import mikufan.cx.vvd.common.label.ValidationPhase
import mikufan.cx.vvd.commonkt.batch.CustomizableBeanRecordValidator
import org.springframework.stereotype.Component
import javax.validation.Validator

@Component
class BeforeProcessLabelValidator(
  validator: Validator
) : CustomizableBeanRecordValidator<VSongLabel>(validator, ValidationPhase.One::class.java)
