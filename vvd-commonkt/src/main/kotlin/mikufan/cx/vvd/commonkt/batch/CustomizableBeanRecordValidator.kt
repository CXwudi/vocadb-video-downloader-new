package mikufan.cx.vvd.commonkt.batch

import jakarta.validation.ConstraintViolation
import jakarta.validation.Validator
import jakarta.validation.groups.Default
import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import org.jeasy.batch.core.record.Record
import org.jeasy.batch.core.validator.RecordValidator

/**
 * This is a reimplementation of easy-batch-validation class but support more flexible settings.
 * @date 2021-06-14
 * @author CX无敌
 */
open class CustomizableBeanRecordValidator<R>(
  private val validator: Validator,
  private vararg val groups: Class<*> = arrayOf(Default::class.java),
) : RecordValidator<R> {

  override fun processRecord(record: Record<R>): Record<R> {
    val constraintViolationSet: Set<ConstraintViolation<Any>> = validator.validate(record.toValidationObject(), *groups)
    if (constraintViolationSet.isNotEmpty()) {
      val stringBuilder = StringBuilder()
      for (constraintViolation in constraintViolationSet) {
        stringBuilder
          .append("Invalid value '").append(constraintViolation.invalidValue).append("' ")
          .append("for property '").append(constraintViolation.propertyPath).append("' : ")
          .append(constraintViolation.message)
          .append("\n")
      }
      throw RuntimeVocaloidException(stringBuilder.toString())
    }
    return record
  }

  /**
   * What to be validated from the record, by default it is the [Record.getPayload]
   *
   * Users can override this to validate anything in the record
   *
   * The return type is [Any] because user can return anything
   */
  open fun Record<R>.toValidationObject(): Any = payload as Any
}
