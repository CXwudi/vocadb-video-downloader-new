package mikufan.cx.vvd.commonkt.batch

import mikufan.cx.vvd.common.exception.RuntimeVocaloidException
import org.jeasy.batch.core.record.Record
import org.jeasy.batch.core.validator.RecordValidator
import javax.validation.ConstraintViolation
import javax.validation.Validator
import javax.validation.groups.Default

/**
 * Recommended to extend this class to avoid declaring too much same type beans.
 *
 * This is a reimplementation of easy-batch-validation class but support more flexible settings.
 * @date 2021-06-14
 * @author CX无敌
 */
open class CustomizableBeanRecordValidator<R>(
  private val validator: Validator,
  private vararg val groups: Class<*> = arrayOf(Default::class.java),
  private val recordMapper: (Record<R>) -> Any = { it.payload as Any }
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
   * an workaround for supporting both @Bean declaration and inheritance
   */
  open fun Record<R>.toValidationObject() = recordMapper(this)
}
