package mikufan.cx.vvd.downloader.config.validation

import mikufan.cx.vvd.downloader.config.Enablement
import mikufan.cx.vvd.downloader.config.Preference
import mu.KotlinLogging
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

/**
 * @date 2021-06-25
 * @author CX无敌
 */

@Constraint(validatedBy = [EnablementValidator::class])
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class IsValidEnablement(
  val message: String = "Not a valid enablement",
  val groups: Array<KClass<*>> = [],
  val payload: Array<KClass<out Payload>> = []
)

@Component
class EnablementValidator(
  private val environment: Environment,
  private val preference: Preference
) : ConstraintValidator<IsValidEnablement, Enablement> {

  /**
   * Check that for each pv service in preference, if there is at least one downloader and all declarded download
   * config are valid
   */
  override fun isValid(enablement: Enablement, context: ConstraintValidatorContext): Boolean {
    var defaultConstraintDisabled = false
    fun disableDefaultConstraintViolation() {
      if (!defaultConstraintDisabled) {
        context.disableDefaultConstraintViolation()
        defaultConstraintDisabled = true
      }
    }
    return preference.pvPreference.map { pvService ->
      val declaredDownloaderNames = enablement[pvService]
      if (declaredDownloaderNames.isEmpty()) {
        disableDefaultConstraintViolation()
        context.buildConstraintViolationWithTemplate("Need at least one downloader for $pvService")
          .addPropertyNode("enablement")
          .addContainerElementNode("<pvService>", Map::class.java, 0)
            .inIterable().atKey(pvService)
          .addConstraintViolation()
        false
      } else {
        val unknownDownloaderNames = declaredDownloaderNames.filterNot { downloaderName ->
          environment.containsProperty("config.downloader.$pvService.$downloaderName.launch-cmd")
        }
        unknownDownloaderNames.forEach { downloaderName ->
          disableDefaultConstraintViolation()
          context.buildConstraintViolationWithTemplate("We don't have a downloader called $downloaderName for $pvService")
            .addPropertyNode("enablement")
            .addContainerElementNode("<pvService>", Map::class.java, 0)
            .inIterable().atKey(pvService)
            // here we really don't know how to reject enablement[pvService][idx] properly
            .addConstraintViolation()
        }
        unknownDownloaderNames.isEmpty().also {
          if (it) log.debug { "$pvService has $declaredDownloaderNames, all checked" }
        }
      }
    }.all { it }
  }
}

private val log = KotlinLogging.logger {}
