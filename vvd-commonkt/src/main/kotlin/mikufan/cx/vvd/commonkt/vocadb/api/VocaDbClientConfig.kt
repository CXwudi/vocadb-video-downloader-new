package mikufan.cx.vvd.commonkt.vocadb.api

import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestClient

/**
 * Factory for creating VocaDB RestClient instances.
 */
class VocaDbClientConfig(
  private val baseUrl: String,
  private val userAgent: String
) {

  /**
   * Build a RestClient with base URL and user agent.
   */
  fun restClient(builder: RestClient.Builder = RestClient.builder()): RestClient {
    return builder
      .baseUrl(baseUrl)
      .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
      .build()
  }

  /**
   * Build a VocaDbClient using the provided RestClient builder.
   */
  fun vocaDbClient(builder: RestClient.Builder = RestClient.builder()): VocaDbClient {
    return VocaDbClient(restClient(builder))
  }
}
