package mikufan.cx.vvd.downloader.config

import mikufan.cx.vocadbapiclient.model.PVServices
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

/**
 * The config data class holding all enabled PV downloaders
 * @date 2021-06-20
 * @author CX无敌
 */
@ConfigurationProperties(prefix = "config") // this is the only way to inject Map from spring configuration
@ConstructorBinding
data class Enablement(
  val enablement: Map<PVServices.Constant, List<String>>
) { // can not use Class : Map<PVServices.Constant, List<String>> by enablement, throw NoSuchMethodException
  operator fun get(key: PVServices.Constant): List<String> = enablement[key] ?: emptyList()
  fun containsKey(key: PVServices.Constant): Boolean = enablement.containsKey(key)
}
