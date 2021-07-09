package mikufan.cx.vvd.downloader.config

import mikufan.cx.vocadbapiclient.model.PVServices
import mikufan.cx.vvd.downloader.config.validation.IsValidEnablement
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated

typealias PVServicesEnum = PVServices.Constant

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
  // consider to submit issue
  operator fun get(key: PVServicesEnum): List<String> = enablement[key] ?: emptyList()
  fun containsPvService(key: PVServicesEnum): Boolean = enablement.containsKey(key)
}