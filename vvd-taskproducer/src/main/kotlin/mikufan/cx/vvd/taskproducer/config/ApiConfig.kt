package mikufan.cx.vvd.taskproducer.config

import mikufan.cx.vocadbapiclient.api.SongApi
import mikufan.cx.vocadbapiclient.api.SongListApi
import mikufan.cx.vocadbapiclient.client.ApiClient
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.validation.annotation.Validated
import org.springframework.web.client.RestTemplate
import javax.validation.Valid

/**
 * @date 2021-05-29
 * @author CX无敌
 */
@Configuration
@Validated
class ApiConfig {

  @Bean
  fun songListApi(apiClient: ApiClient) = SongListApi(apiClient)

  @Bean
  fun songApi(apiClient: ApiClient) = SongApi(apiClient)

  @Bean
  fun apiClient(restTemplate: RestTemplate, @Valid systemConfig: SystemConfig): ApiClient =
    ApiClient(restTemplate)
      .setBasePath(systemConfig.baseUrl)
      .setUserAgent(systemConfig.userAgent)

  @Bean
  fun restTemplate(restTemplateBuilder: RestTemplateBuilder): RestTemplate =
    restTemplateBuilder.requestFactory {
      // simulate how ApiClient build restTemplate
      BufferingClientHttpRequestFactory(restTemplateBuilder.buildRequestFactory())
    }.build()
}

