package mikufan.cx.vvd.downloader.config.enablement

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import mikufan.cx.inlinelogging.KInlineLogging
import mikufan.cx.vvd.downloader.config.preference.Preference
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

/**
 * @date 2021-06-25
 * @author CX无敌
 */

@Constraint(validatedBy = [EnablementValidator::class])
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
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
    // for each enabled pv service
    return preference.pvPreference.map { pvService ->
      val declaredDownloaderNames = enablement[pvService]
      // first check if no downloader configured
      if (declaredDownloaderNames.isEmpty()) {
        disableDefaultConstraintViolation()
        context.buildConstraintViolationWithTemplate("Need at least one downloader for $pvService")
          .addPropertyNode("enablement")
          .addContainerElementNode("<pvService>", Map::class.java, 0)
            .inIterable().atKey(pvService)
          .addConstraintViolation()
        false
      } else {
        // then check if downloader names are correct
        val unknownDownloaderNames = declaredDownloaderNames.filterNot { downloaderName ->
          // by checking if a property exists, not necessarily checking if it is not empty
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

private val log = KInlineLogging.logger()
