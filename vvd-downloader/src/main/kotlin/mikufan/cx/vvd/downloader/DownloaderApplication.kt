package mikufan.cx.vvd.downloader

import mikufan.cx.vvd.downloader.service.MainService
import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

/**
 * @date 2021-06-15
 * @author CX无敌
 */
@SpringBootApplication
@ConfigurationPropertiesScan
class DownloaderApplication

fun main(args: Array<String>) {
  runApplication<DownloaderApplication>(*args).use { it.getBean<MainService>().run() }
}
