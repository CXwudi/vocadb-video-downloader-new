package mikufan.cx.vvd.downloader.config.enablement

import mikufan.cx.vvd.downloader.util.PVServicesEnum
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated

/**
 * The config data class holding all enabled PV downloaders
 * @date 2021-06-20
 * @author CX无敌
 */
@ConfigurationProperties(prefix = "config") // this is the only way to inject Map from spring configuration
@ConstructorBinding
@Validated
@IsValidEnablement
data class Enablement(
  val enablement: Map<PVServicesEnum, List<String>>
) { // can not use Class : Map<PVServices.Constant, List<String>> by enablement, throw NoSuchMethodException,
  // guess that is caused by Spring recognizing it as a Map<>
  // consider submitting issue
  operator fun get(key: PVServicesEnum): List<String> = enablement[key] ?: emptyList()
  fun containsPvService(key: PVServicesEnum): Boolean = enablement.containsKey(key)
}
