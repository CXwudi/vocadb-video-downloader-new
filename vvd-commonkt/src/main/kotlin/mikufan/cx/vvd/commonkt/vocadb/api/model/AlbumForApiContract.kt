package mikufan.cx.vvd.commonkt.vocadb.api.model

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import tools.jackson.databind.JsonNode

/**
 * Minimal album contract used for tags.
 */
data class AlbumForApiContract(
  val name: String? = null,
  @param:JsonAnySetter
  @get:JsonAnyGetter
  val additionalProperties: Map<String, JsonNode> = emptyMap()
)

