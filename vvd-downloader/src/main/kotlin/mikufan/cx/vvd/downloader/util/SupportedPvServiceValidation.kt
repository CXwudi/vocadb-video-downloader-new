package mikufan.cx.vvd.downloader.util

import mikufan.cx.vocadbapiclient.model.PVServices
import org.springframework.util.CollectionUtils
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

/**
 * A set of currently supported PV services
 * @date 2021-06-18
 * @author CX无敌
 */

val SUPPORTED_SERVICES = setOf(
  PVServices.Constant.NICONICODOUGA,
  PVServices.Constant.YOUTUBE,
  PVServices.Constant.BILIBILI,
)

/**
 * Only use it on a List of <PVServices.Constant> to check
 * if this list contains only elements in [SUPPORTED_SERVICES]
 * @author CX无敌
 * @date 2020-12-21
 */
@Constraint(validatedBy = [SupportPvServicesValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY, AnnotationTarget.PROPERTY_GETTER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AreSupportedPvServices(
  val message: String = "List containing unsupported pv services",
  val groups: Array<KClass<*>> = [],
  val payload: Array<KClass<out Payload>> = []
)

/**
 * @author CX无敌
 * @date 2020-12-21
 */
class SupportPvServicesValidator : ConstraintValidator<AreSupportedPvServices, List<PVServices.Constant>> {

  override fun isValid(value: List<PVServices.Constant>?, context: ConstraintValidatorContext): Boolean {
    context.disableDefaultConstraintViolation()
    return when {
      CollectionUtils.isEmpty(value) -> {
        true
      }
      else -> {
        val unsupportedServices = value!!.filterNot { SUPPORTED_SERVICES.contains(it) }
        if (unsupportedServices.isEmpty()){
          true
        } else {
          context
            .buildConstraintViolationWithTemplate(
              "${unsupportedServices.joinToString()} " +
                "${when(unsupportedServices.size) {
                  1 -> "is"
                  else -> "are"}
                } not supported yet")
            .addConstraintViolation()
          false
        }
      }
    }
  }
}
