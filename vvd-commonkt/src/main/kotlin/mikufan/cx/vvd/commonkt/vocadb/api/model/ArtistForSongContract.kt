package mikufan.cx.vvd.commonkt.vocadb.api.model

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.databind.JsonNode

/**
 * Minimal artist info used for artist field fixing.
 */
data class ArtistForSongContract(
  val name: String? = null,
  val categories: ArtistCategories? = null,
  @param:JsonAnySetter
  @get:JsonAnyGetter
  val additionalProperties: Map<String, JsonNode> = emptyMap()
)
