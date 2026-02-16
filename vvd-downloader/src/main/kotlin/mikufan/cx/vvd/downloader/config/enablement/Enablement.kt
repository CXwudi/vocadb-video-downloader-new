package mikufan.cx.vvd.downloader.config.enablement

import mikufan.cx.vvd.commonkt.vocadb.api.model.PVService
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

/**
 * The config data class holding all enabled PV downloaders
 * @date 2021-06-20
 * @author CX无敌
 */
@ConfigurationProperties(prefix = "config") // this is the only way to inject Map from spring configuration

@Validated
@IsValidEnablement
data class Enablement(
  val enablement: Map<PVService, List<String>>
) { // can not use Class : Map<PVService, List<String>> by enablement, throw NoSuchMethodException,
  // guess that is caused by Spring recognizing it as a Map<>
  // consider submitting issue
  operator fun get(key: PVService): List<String> = enablement[key] ?: emptyList()
  fun containsPvService(key: PVService): Boolean = enablement.containsKey(key)
}
