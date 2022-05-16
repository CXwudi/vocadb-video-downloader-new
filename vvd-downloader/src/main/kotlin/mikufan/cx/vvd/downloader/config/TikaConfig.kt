package mikufan.cx.vvd.downloader.config

import org.apache.tika.Tika
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @date 2022-05-16
 * @author CX无敌
 */
@Configuration
class TikaConfig {
  @Bean
  fun tika() = Tika()
}
