package mikufan.cx.vvd.taskproducer.config

import jakarta.validation.Valid
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import org.springframework.web.client.RestClient

/**
 * @date 2021-05-29
 * @author CX无敌
 */
@Configuration
@Validated
class ApiConfig {

  @Bean
  fun vocaDbClient(
    restClientBuilder: RestClient.Builder,
    @Valid systemConfig: SystemConfig
  ): VocaDbClient {
    val config = VocaDbClientConfig(systemConfig.baseUrl, systemConfig.userAgent)
    return config.vocaDbClient(restClientBuilder)
  }
}

