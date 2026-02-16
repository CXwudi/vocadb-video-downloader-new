package mikufan.cx.vvd.taskproducer.component.api

import mikufan.cx.vvd.commonkt.vocadb.api.model.SongForApiContract
import mikufan.cx.vvd.commonkt.vocadb.api.model.SongInListForApiContractPartialFindResult
import mikufan.cx.vvd.commonkt.vocadb.api.model.SongOptionalFields
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient

/**
 * Client for VocaDB public API endpoints used by this project.
 */
class VocaDbClient(
  private val restClient: RestClient
) {

  /**
   * Fetch songs in a list with pagination and optional fields.
   */
  fun getSongListSongs(
    listId: Int,
    start: Int,
    maxResults: Int,
    getTotalCount: Boolean = true,
    fields: SongOptionalFields? = null
  ): SongInListForApiContractPartialFindResult {
    require(start >= 0) { "start must be >= 0" }
    require(maxResults > 0) { "maxResults must be > 0" }

    val response = restClient
      .get()
      .uri { builder ->
        val uriBuilder = builder
          .path("/api/songLists/{listId}/songs")
          .queryParam("start", start)
          .queryParam("maxResults", maxResults)
          .queryParam("getTotalCount", getTotalCount)
        fields?.toJson()?.takeIf { it.isNotBlank() }?.let { value ->
          uriBuilder.queryParam("fields", value)
        }
        uriBuilder.build(listId)
      }
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .body(SongInListForApiContractPartialFindResult::class.java)

    return requireNotNull(response) { "Response body is null for list $listId" }
  }

  /**
   * Fetch a song by ID with optional fields.
   */
  fun getSongById(
    id: Int,
    fields: SongOptionalFields? = null
  ): SongForApiContract {
    val response = restClient
      .get()
      .uri { builder ->
        val uriBuilder = builder.path("/api/songs/{id}")
        fields?.toJson()?.takeIf { it.isNotBlank() }?.let { value ->
          uriBuilder.queryParam("fields", value)
        }
        uriBuilder.build(id)
      }
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .body(SongForApiContract::class.java)

    return requireNotNull(response) { "Response body is null for song $id" }
  }
}
