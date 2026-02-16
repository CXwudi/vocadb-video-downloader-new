package mikufan.cx.vvd.taskproducer.config

import org.springframework.http.HttpHeaders
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient

/**
 * Factory for creating VocaDB RestClient instances.
 */
class VocaDbClientConfig(
  private val baseUrl: String,
  private val userAgent: String
) {
  companion object {
    private const val CONNECT_TIMEOUT_MS = 10_000
    private const val READ_TIMEOUT_MS = 30_000
  }

  /**
   * Build a RestClient with base URL and user agent.
   */
  fun restClient(builder: RestClient.Builder): RestClient {
    val requestFactory = SimpleClientHttpRequestFactory().apply {
      setConnectTimeout(CONNECT_TIMEOUT_MS)
      setReadTimeout(READ_TIMEOUT_MS)
    }
    return builder
      .requestFactory(requestFactory)
      .baseUrl(baseUrl)
      .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
      .build()
  }

  /**
   * Build a VocaDbClient using the provided RestClient builder.
   */
  fun vocaDbClient(builder: RestClient.Builder): VocaDbClient {
    return VocaDbClient(restClient(builder))
  }
}
