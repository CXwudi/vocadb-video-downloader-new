package mikufan.cx.vvd.extractor

import mikufan.cx.vvd.extractor.service.MainService
import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

/**
 * @date 2022-06-11
 * @author CX无敌
 */
@SpringBootApplication
@ConfigurationPropertiesScan
class ExtractorApplication

fun main(args: Array<String>) {
  runApplication<ExtractorApplication>(*args).use { it.getBean<MainService>().run() }
}
