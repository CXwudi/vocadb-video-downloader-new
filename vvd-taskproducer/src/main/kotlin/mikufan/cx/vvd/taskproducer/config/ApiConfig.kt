package mikufan.cx.vvd.taskproducer.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import mikufan.cx.vvd.taskproducer.component.api.VocaDbClient

/**
 * @date 2021-05-29
 * @author CX无敌
 */
@Configuration
class ApiConfig {

  companion object {
    private const val CONNECT_TIMEOUT_MS = 10_000
    private const val READ_TIMEOUT_MS = 30_000
  }

  @Bean
  fun restClient(
    restClientBuilder: RestClient.Builder,
    systemConfig: SystemConfig
  ): RestClient {
    val requestFactory = SimpleClientHttpRequestFactory().apply {
      setConnectTimeout(CONNECT_TIMEOUT_MS)
      setReadTimeout(READ_TIMEOUT_MS)
    }
    return restClientBuilder
      .requestFactory(requestFactory)
      .baseUrl(systemConfig.baseUrl)
      .defaultHeader(HttpHeaders.USER_AGENT, systemConfig.userAgent)
      .build()
  }

  @Bean
  fun vocaDbClient(
    restClient: RestClient
  ): VocaDbClient {
    return VocaDbClient(restClient)
  }
}

